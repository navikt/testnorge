package no.nav.pdl.forvalter.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.dto.PdlBestillingResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.Callable;

import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.TemaGrunnlag.GEN;

@Slf4j
@RequiredArgsConstructor
public class PdlTestdataCommand implements Callable<PdlBestillingResponse> {

    public static final String HEADER_NAV_PERSON_IDENT = "Nav-Personident";
    private static final String TEMA = "Tema";
    private final WebClient webClient;
    private final String url;
    private final String ident;
    private final Object body;
    private final String token;

    @Override
    public PdlBestillingResponse call() {

        return webClient
                .post()
                .uri(builder -> builder.path(url).build())
                .body(BodyInserters.fromValue(body))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(TEMA, GEN.name())
                .header(HEADER_NAV_PERSON_IDENT, ident)
                .retrieve()
                .bodyToMono(PdlBestillingResponse.class)
                .block();
    }
}
