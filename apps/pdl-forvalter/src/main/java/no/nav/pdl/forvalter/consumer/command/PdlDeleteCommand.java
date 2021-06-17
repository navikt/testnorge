package no.nav.pdl.forvalter.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.dto.PdlBestillingResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;

import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.TemaGrunnlag.GEN;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Slf4j
@RequiredArgsConstructor
public class PdlDeleteCommand implements Callable<PdlBestillingResponse> {

    private static final String HEADER_NAV_PERSON_IDENT = "Nav-Personident";
    private static final String TEMA = "Tema";

    private final WebClient webClient;
    private final String url;
    private final String ident;
    private final String token;

    @Override
    public PdlBestillingResponse call() {

        var response = webClient
                .delete()
                .uri(builder -> builder.path(url).build())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(TEMA, GEN.name())
                .header(HEADER_NAV_PERSON_IDENT, ident)
                .retrieve()
                .bodyToMono(PdlBestillingResponse.class)
                .block();

        if (isBlank(response.getFeilmelding())) {
            return response;

        } else {
            throw new WebClientResponseException(BAD_REQUEST.value(), "Sletting feilet",
                    null, ("Sletting feilet: " + response.getFeilmelding()).getBytes(StandardCharsets.UTF_8),
                    StandardCharsets.UTF_8);
        }
    }
}
