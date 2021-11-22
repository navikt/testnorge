package no.nav.registre.testnorge.arbeidsforholdservice.consumer.command;


import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.arbeidsforholdservice.consumer.dto.ArbeidsforholdDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class GetArbeidstakerArbeidsforholdCommand implements Callable<List<ArbeidsforholdDTO>> {
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
                    .uri(builder -> builder
                            .path("/api/{miljo}/v1/arbeidstaker/arbeidsforhold")
                            .queryParam("arbeidsforholdtype", "forenkletOppgjoersordning", "frilanserOppdragstakerHonorarPersonerMm", "maritimtArbeidsforhold", "ordinaertArbeidsforhold")
                            .build(miljo))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .header(NAV_PERSON_IDENT, ident)
                    .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                    .header(HEADER_NAV_CALL_ID, getNavCallId())
                    .retrieve()
                    .bodyToMono(ArbeidsforholdDTO[].class)
                    .block();

            log.info("Hentet arbeidsforhold fra Aareg: " + Arrays.toString(arbeidsforhold));
            return Arrays.stream(arbeidsforhold).collect(Collectors.toList());
        } catch (WebClientResponseException.NotFound e) {
            log.warn("Fant ikke arbeidsforhold for ident {} i miljø {}", ident, miljo);
            return Collections.emptyList();
        } catch (WebClientResponseException e) {
            log.error(
                    "Klarer ikke å hente arbeidsforhold for ident: {}. Feilmelding: {}.",
                    ident,
                    e.getResponseBodyAsString()
            );
            throw e;
        }
    }
}
