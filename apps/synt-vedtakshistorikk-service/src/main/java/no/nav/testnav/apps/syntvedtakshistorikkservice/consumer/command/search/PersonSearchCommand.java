package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command.search;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.search.PersonSearchResponse;
import no.nav.testnav.libs.dto.dollysearchservice.v1.legacy.PersonDTO;
import no.nav.testnav.libs.dto.dollysearchservice.v1.legacy.PersonSearch;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class PersonSearchCommand implements Callable<Mono<PersonSearchResponse>> {

    private final PersonSearch request;
    private final String token;
    private final WebClient webClient;

    private static final ParameterizedTypeReference<List<PersonDTO>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };
    private static final String NUMBER_OF_ITEMS_HEADER = "NUMBER_OF_ITEMS";

    @Override
    public Mono<PersonSearchResponse> call() {
        return webClient
                .post()
                .uri(builder -> builder.path("/api/v1/legacy").build())
                .headers(WebClientHeader.bearer(token))
                .body(BodyInserters.fromPublisher(Mono.just(request), PersonSearch.class))
                .retrieve()
                .toEntity(RESPONSE_TYPE)
                .flatMap(entity -> {
                    var headers = entity.getHeaders().get(NUMBER_OF_ITEMS_HEADER);
                    var numberOfItems = headers != null && !headers.isEmpty() ? headers.getFirst() : "0";
                    return Mono.just(new PersonSearchResponse(Integer.parseInt(numberOfItems), entity.getBody()));
                })
                .doOnError(WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(error -> Mono.empty());
    }

}
