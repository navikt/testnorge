package no.nav.registre.testnorge.personexportapi.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.personexportapi.consumer.command.GetTpsfGrupperCommand;
import no.nav.registre.testnorge.personexportapi.consumer.command.GetTpsfMeldingerFromPageCommand;
import no.nav.registre.testnorge.personexportapi.consumer.dto.GruppeDTO;
import no.nav.registre.testnorge.personexportapi.domain.Person;

@Slf4j
@Component
@DependencyOn(value = "tps-forvalter", external = true)
public class TpsfConsumer {
    private final WebClient webClient;
    private final String username;
    private final String password;
    private final ExecutorService executorService;

    public TpsfConsumer(
            @Value("${consumers.tps-forvalter.username}") String username,
            @Value("${consumers.tps-forvalter.password}") String password,
            @Value("${consumers.tps-forvalter.url}") String baseUrl,
            @Value("${consumers.tps-forvalter.threads}") Integer threads
    ) {
        this.username = username;
        this.password = password;
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
        this.executorService = Executors.newFixedThreadPool(threads);
    }


    private GruppeDTO getGruppe(String avspillingsgruppe) {
        log.info("Henter avspillingsgruppe med id {}...", avspillingsgruppe);
        List<GruppeDTO> list = new GetTpsfGrupperCommand(webClient, username, password).call();
        GruppeDTO gruppeDTO = list.stream()
                .filter(value -> value.getId().equals(avspillingsgruppe))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Finner ikke gruppe med avspillergruppeId " + avspillingsgruppe));
        log.info("Avspillingsgruppe med id {} og navn {} hentet.", gruppeDTO.getId(), gruppeDTO.getNavn());
        return gruppeDTO;
    }

    private int getNumberOfPages(String avspillingsgruppe) {
        int antallSider = getGruppe(avspillingsgruppe).getAntallSider();
        log.info("Gruppe med avspillingsgruppe id {} har {} antall sider.", avspillingsgruppe, antallSider);
        return antallSider;
    }

    private CompletableFuture<List<Person>> getPersonFromPage(String avspillingsgruppe, int page, int numberOfPages) {
        return CompletableFuture.supplyAsync(
                () -> new GetTpsfMeldingerFromPageCommand(webClient, username, password, avspillingsgruppe, page).call(),
                executorService
        ).thenApply(meldinger -> {
            log.info("Har hentet {}/{} sider fra avspillingsgruppeID {}", page + 1, numberOfPages, avspillingsgruppe);
            return meldinger
                    .stream()
                    .filter(value -> value.isFoedsel() || value.isInnvandring())
                    .map(Person::new)
                    .collect(Collectors.toList());
        });
    }

    public List<Person> getPersoner(String avspillingsgruppe) {
        int numberOfPages = getNumberOfPages(avspillingsgruppe);
        log.info("Henter alle personer fra avspillingsgruppeID {} med {} antall sider.", avspillingsgruppe, numberOfPages);

        List<CompletableFuture<List<Person>>> futures = new ArrayList<>();
        for (int page = 0; page < numberOfPages; page++) {
            futures.add(getPersonFromPage(avspillingsgruppe, page, numberOfPages));
        }
        List<Person> personer = new ArrayList<>();
        for (CompletableFuture<List<Person>> future : futures) {
            try {
                personer.addAll(future.get());
            } catch (Exception e) {
                executorService.shutdown();
                throw new RuntimeException("Klare ikke a hente ut alle personer.", e);
            }
        }
        return personer;
    }
}
