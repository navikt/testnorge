package no.nav.dolly.bestilling.pdldata.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonUpdateRequestDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class PdlDataOppdateringCommand implements Callable<Mono<String>> {

    private static final String PDL_FORVALTER_URL = "/api/v1/personer/{ident}";

    private final WebClient webClient;
    private final String ident;
    private final PersonUpdateRequestDTO body;
    private final String token;

    public Mono<String> call() {

        return webClient
                .put()
                .uri(PDL_FORVALTER_URL, ident)
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(body))
                .retrieve()
                .bodyToMono(String.class);
    }
}
