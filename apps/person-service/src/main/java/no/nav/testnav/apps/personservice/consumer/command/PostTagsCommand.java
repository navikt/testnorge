package no.nav.testnav.apps.personservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.personservice.domain.Person;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class PostTagsCommand implements Callable<Mono<Void>> {

    private static final ParameterizedTypeReference<List<String>> REQUEST_TYPE = new ParameterizedTypeReference<>() {};
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
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(BodyInserters.fromPublisher(Mono.just(ident),REQUEST_TYPE))
                .retrieve()
                .bodyToMono(Void.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}