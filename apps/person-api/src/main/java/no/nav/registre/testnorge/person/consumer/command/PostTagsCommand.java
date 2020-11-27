package no.nav.registre.testnorge.person.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import no.nav.registre.testnorge.person.domain.Person;

@Slf4j
@RequiredArgsConstructor
public class PostTagsCommand implements Callable<Void> {

    private static final ParameterizedTypeReference<List<String>> REQUEST_TYPE = new ParameterizedTypeReference<>() {};
    private final WebClient webClient;
    private final Person person;
    private final String token;

    @Override
    public Void call() {
        List<String> ident = Collections.singletonList(person.getIdent());
        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/bestilling/tags")
                        .queryParam("tags", person.getTags().toArray())
                        .build()
                )
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(BodyInserters.fromPublisher(Mono.just(ident),REQUEST_TYPE))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}