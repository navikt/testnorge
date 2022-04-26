package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command.search;

import lombok.AllArgsConstructor;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.search.PersonSearchResponse;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.util.WebClientFilter;
import no.nav.testnav.libs.dto.personsearchservice.v1.PersonDTO;
import no.nav.testnav.libs.dto.personsearchservice.v1.search.PersonSearch;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;

import static no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.util.Headers.AUTHORIZATION;

@AllArgsConstructor
public class PersonSearchCommand implements Callable<Mono<PersonSearchResponse>> {

    private final PersonSearch request;
    private final String token;
    private final WebClient webClient;

    private static final ParameterizedTypeReference<List<PersonDTO>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };
    private static final String NUMBER_OF_ITEMS_HEADER = "NUMBER_OF_ITEMS";

    @Override
    public Mono<PersonSearchResponse> call() {
        return webClient.post()
                .uri(builder ->
                        builder.path("/api/v1/person")
                                .build()
                )
                .header(AUTHORIZATION, "Bearer " + token)
                .body(BodyInserters.fromPublisher(Mono.just(request), PersonSearch.class))
                .retrieve()
                .toEntity(RESPONSE_TYPE)
                .flatMap(entity -> {
                    var headers = entity.getHeaders().get(NUMBER_OF_ITEMS_HEADER);
                    var numberOfItems = headers != null && !headers.isEmpty() ? headers.get(0) : "0";
                    return Mono.just(new PersonSearchResponse(Integer.parseInt(numberOfItems), entity.getBody()));
                })
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
