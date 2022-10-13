package no.nav.testnav.apps.personexportapi.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.personexportapi.consumer.command.GetTpsfGrupperCommand;
import no.nav.testnav.apps.personexportapi.consumer.command.GetTpsfMeldingerFromPageCommand;
import no.nav.testnav.apps.personexportapi.consumer.credential.TpsfProperties;
import no.nav.testnav.apps.personexportapi.consumer.dto.GruppeDTO;
import no.nav.testnav.apps.personexportapi.domain.Person;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Slf4j
@Component
public class TpsfConsumer {
    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties properties;
    private final ExecutorService executorService;

    public TpsfConsumer(
            TokenExchange tokenExchange,
            TpsfProperties serviceProperties,
            @Value("${consumers.tps-forvalter.threads}") Integer threads,
            ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.tokenExchange = tokenExchange;
        this.properties = serviceProperties;
        this.webClient = WebClient.builder()
                .baseUrl(properties.getUrl())
                .filter(metricsWebClientFilterFunction)
                .build();
        this.executorService = Executors.newFixedThreadPool(threads);
    }

    private GruppeDTO getGruppe(String avspillingsgruppe) {
        log.info("Henter avspillingsgruppe med id {}...", avspillingsgruppe);

        var list = tokenExchange.exchange(properties)
                .flatMap(accessToken -> new GetTpsfGrupperCommand(webClient, accessToken.getTokenValue()).call())
                .block();

        var gruppeDTO = list.stream()
                .filter(value -> value.getId().equals(avspillingsgruppe))
                .findFirst()
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Finner ikke gruppe med avspillergruppeId " + avspillingsgruppe));
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
                () -> tokenExchange.exchange(properties)
                        .flatMap(accessToken -> new GetTpsfMeldingerFromPageCommand(
                                webClient, accessToken.getTokenValue(), avspillingsgruppe, page).call())
                        .block(),
                executorService
        ).thenApply(meldinger -> {
            log.info("Har hentet {}/{} sider fra avspillingsgruppeID {}", page + 1, numberOfPages, avspillingsgruppe);
            return meldinger
                    .stream()
                    .filter(value -> value.isFoedsel() || value.isInnvandring())
                    .map(endringsmelding -> new Person(endringsmelding, format("%d/%d", page + 1, numberOfPages)))
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
                log.info(format("Active threads: %d, Waiting to start: %d",
                        ((ThreadPoolExecutor) executorService).getActiveCount(),
                        ((ThreadPoolExecutor) executorService).getQueue().size()));
                personer.addAll(future.get(1, TimeUnit.MINUTES));

            } catch (TimeoutException e) {
                log.error("Future task timeout exception {}", future, e);
            } catch (ExecutionException e) {
                log.error("Execution exception", e);
            } catch (InterruptedException e) {
                log.error("Interrupted exception", e);
            }
        }
        return personer;
    }
}
