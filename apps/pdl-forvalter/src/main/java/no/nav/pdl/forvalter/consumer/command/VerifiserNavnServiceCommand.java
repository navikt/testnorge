package no.nav.pdl.forvalter.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.libs.dto.generernavnservice.v1.NavnDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class VerifiserNavnServiceCommand implements Callable<Mono<Boolean>> {

    private final WebClient webClient;
    private final String url;
    private final NavnDTO body;
    private final String token;

    @Override
    public Mono<Boolean> call() {

        try {
            return webClient
                    .post()
                    .uri(builder -> builder.path(url).build())
                    .body(BodyInserters.fromValue(body))
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .bodyToMono(Boolean.class);

        } catch (
                WebClientResponseException e) {
            log.error("Verifisering av navn fra navneservice feilet {}.", e.getResponseBodyAsString(), e);
            throw e;
        }
    }
}
