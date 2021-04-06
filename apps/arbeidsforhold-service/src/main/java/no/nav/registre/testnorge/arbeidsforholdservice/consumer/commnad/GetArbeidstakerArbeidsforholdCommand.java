package no.nav.registre.testnorge.arbeidsforholdservice.consumer.commnad;


import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.arbeidsforholdservice.consumer.dto.ArbeidsforholdDTO;
import no.nav.registre.testnorge.arbeidsforholdservice.domain.Arbeidsforhold;

@Slf4j
@RequiredArgsConstructor
public class GetArbeidstakerArbeidsforholdCommand implements Callable<List<Arbeidsforhold>> {
    private static final String NAV_PERSON_IDENT = "Nav-Personident";
    private final WebClient webClient;
    private final String miljo;
    private final String token;
    private final String ident;

    @SneakyThrows
    @Override
    public List<Arbeidsforhold> call() {
        try {
            var arbeidsforhold = webClient
                    .get()
                    .uri(builder -> builder.path("/api/{miljo}/v1/arbeidstaker/arbeidsforhold").build(miljo))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .header(NAV_PERSON_IDENT, ident)
                    .retrieve()
                    .bodyToMono(ArbeidsforholdDTO[].class)
                    .block();
            return Arrays.stream(arbeidsforhold).map(Arbeidsforhold::new).collect(Collectors.toList());
        } catch (WebClientResponseException e) {
            log.error(
                    "Klarer ikke å hente arbeidsforhold for {}. Feilmelding: {}.",
                    ident,
                    e.getResponseBodyAsString()
            );
            throw e;
        }
    }
}
