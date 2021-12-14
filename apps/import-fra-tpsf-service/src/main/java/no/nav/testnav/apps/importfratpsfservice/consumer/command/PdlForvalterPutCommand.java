package no.nav.testnav.apps.importfratpsfservice.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class PdlForvalterPutCommand implements Callable<Mono<String>> {

    private static final String PDL_PERSON_URL = "/api/v1/personer/{ident}";

    private final WebClient webClient;
    private final String ident;
    private final PersonDTO person;
    private final String token;

    @Override
    public Mono<String> call() {

        return webClient
                .put()
                .uri(builder -> builder.path(PDL_PERSON_URL).build(ident))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(BodyInserters.fromValue(person))
                .retrieve()
                .bodyToMono(String.class);
    }
}
