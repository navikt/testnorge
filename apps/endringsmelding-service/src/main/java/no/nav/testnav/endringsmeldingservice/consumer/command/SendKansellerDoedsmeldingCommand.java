package no.nav.testnav.endringsmeldingservice.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.DoedsmeldingResponse;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.PersonDTO;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class SendKansellerDoedsmeldingCommand implements Callable<Mono<DoedsmeldingResponse>> {

    private static final String DOEDSMELDING_URL = "/api/v1/personer/doedsmelding";
    private static final String MILJOER = "miljoer";

    private final WebClient webClient;
    private final PersonDTO person;
    private final Set<String> miljoer;
    private final String token;

    @Override
    public Mono<DoedsmeldingResponse> call() {

        return webClient
                .method(HttpMethod.DELETE)
                .uri(builder -> builder.path(DOEDSMELDING_URL)
                        .queryParam(MILJOER, miljoer)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(BodyInserters.fromValue(person))
                .retrieve()
                .bodyToMono(DoedsmeldingResponse.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .doOnError(WebClientFilter::logErrorMessage);
    }
}
