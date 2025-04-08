package no.nav.testnav.apps.personservice.consumer.v1.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.personservice.domain.Person;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class PostTagsCommand implements Callable<Mono<Void>> {

    private static final ParameterizedTypeReference<List<String>> REQUEST_TYPE = new ParameterizedTypeReference<>() {
    };
    private final WebClient webClient;
    private final Person person;
    private final String token;

    @Override
    public Mono<Void> call() {
        List<String> ident = Collections.singletonList(person.getIdent());
        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/pdl-testdata/api/v1/bestilling/tags")
                        .queryParam("tags", person.getTags().toArray())
                        .build()
                )
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .headers(WebClientHeader.bearer(token))
                .body(BodyInserters.fromPublisher(Mono.just(ident), REQUEST_TYPE))
                .retrieve()
                .bodyToMono(Void.class)
                .retryWhen(WebClientError.is5xxException());
    }

}