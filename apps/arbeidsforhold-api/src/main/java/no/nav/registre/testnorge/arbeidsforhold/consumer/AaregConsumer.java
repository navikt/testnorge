package no.nav.registre.testnorge.arbeidsforhold.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.arbeidsforhold.consumer.commnad.CreateArbeidsforholdCommand;
import no.nav.registre.testnorge.arbeidsforhold.consumer.commnad.GetArbeidsforholdCommand;
import no.nav.registre.testnorge.arbeidsforhold.consumer.commnad.GetArbeidsforholdoversiktCommand;
import no.nav.registre.testnorge.arbeidsforhold.consumer.commnad.GetArbeidstakerArbeidsforholdCommand;
import no.nav.registre.testnorge.arbeidsforhold.consumer.dto.ArbeidsforholdoversikterDTO;
import no.nav.registre.testnorge.arbeidsforhold.domain.Arbeidsforhold;
import no.nav.registre.testnorge.arbeidsforhold.service.StsOidcTokenService;

@Slf4j
@Component
public class AaregConsumer {

    private final StsOidcTokenService tokenService;
    private final RestTemplate restTemplate;
    private final String url;
    private final ExecutorService executorService;

    public AaregConsumer(StsOidcTokenService tokenService, RestTemplateBuilder restTemplateBuilder, @Value("${aareg.url}") String url) {
        this.tokenService = tokenService;
        this.restTemplate = restTemplateBuilder.build();
        this.url = url;
        this.executorService = Executors.newFixedThreadPool(5);
    }

    private CompletableFuture<Arbeidsforhold> getArbeidsforhold(Integer navArbeidsforholdId, String miljo) {
        return CompletableFuture.supplyAsync(
                () -> new GetArbeidsforholdCommand(restTemplate, url, tokenService.getToken(), navArbeidsforholdId, miljo).call(),
                executorService
        );
    }


    public List<Arbeidsforhold> getArbeidsforholdByArbeidsgiver(String orgummer, String miljo) {
        log.info("Henter alle arbeidsforhold for arbeidsgiver {}...", orgummer);
        ArbeidsforholdoversikterDTO arbeidsforholdoversikter =
                new GetArbeidsforholdoversiktCommand(restTemplate, url, tokenService.getToken(), orgummer, miljo).call();

        var futures = arbeidsforholdoversikter.getArbeidsforholdoversikter()
                .stream()
                .map(value -> getArbeidsforhold(value.getNavArbeidsforholdId(), miljo))
                .collect(Collectors.toList());

        List<Arbeidsforhold> arbeidsforholds = new ArrayList<>();
        for (int index = 0; index < futures.size(); index++) {
            try {
                arbeidsforholds.add(futures.get(index).get());
                log.info("Hentet antall arbeisforhold {}/{}", index, futures.size());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException("Klarer ikke a hente ut arbeidsforhold", e);
            }
        }
        log.info("Hentet alle arbeidsforhold for arbeidsgiver {}", orgummer);
        return arbeidsforholds;
    }

    public Arbeidsforhold createArbeidsforhold(Arbeidsforhold arbeidsforhold) {
        return new CreateArbeidsforholdCommand(restTemplate, url, tokenService.getToken(), arbeidsforhold).call();
    }

    public List<Arbeidsforhold> getArbeidsforholds(String ident) {
        return new GetArbeidstakerArbeidsforholdCommand(restTemplate, url, tokenService.getToken(), ident).call();
    }

    public List<Arbeidsforhold> getArbeidsforholds(String ident, String orgnummer) {
        return getArbeidsforholds(ident)
                .stream()
                .filter(value -> value.getOrgnummer().equals(orgnummer))
                .collect(Collectors.toList());
    }

    public Arbeidsforhold getArbeidsforhold(String ident, String orgnummer, String arbeidsforholdId) {
        return getArbeidsforholds(ident, orgnummer)
                .stream()
                .filter(value -> value.getArbeidsforholdId().equals(arbeidsforholdId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Klarer ikke aa finne arbeidsforhold for " + ident));
    }
}