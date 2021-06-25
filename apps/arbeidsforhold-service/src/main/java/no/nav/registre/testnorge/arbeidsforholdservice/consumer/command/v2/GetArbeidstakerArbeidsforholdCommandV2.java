package no.nav.registre.testnorge.arbeidsforholdservice.consumer.command.v2;


import io.swagger.v3.core.util.Json;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v2.ArbeidsforholdDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class GetArbeidstakerArbeidsforholdCommandV2 implements Callable<List<ArbeidsforholdDTO>> {
    private static final String NAV_PERSON_IDENT = "Nav-Personident";
    private final WebClient webClient;
    private final String miljo;
    private final String token;
    private final String ident;

    @SneakyThrows
    @Override
    public List<ArbeidsforholdDTO> call() {
        try {
            var arbeidsforhold = webClient
                    .get()
                    .uri(builder -> builder.path("/api/{miljo}/v1/arbeidstaker/arbeidsforhold").build(miljo))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .header(NAV_PERSON_IDENT, ident)
                    .retrieve()
                    .bodyToMono(ArbeidsforholdDTO[].class)
                    .block();
            log.info("Hentet arbeidsforhold fra Aareg: " + Json.pretty(arbeidsforhold));
            return Arrays.stream(arbeidsforhold).collect(Collectors.toList());
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
