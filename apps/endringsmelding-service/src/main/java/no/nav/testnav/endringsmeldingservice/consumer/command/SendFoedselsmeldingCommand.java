package no.nav.testnav.endringsmeldingservice.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.FoedselsmeldingRequest;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.FoedselsmeldingResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class SendFoedselsmeldingCommand implements Callable<Mono<FoedselsmeldingResponse>> {

    private static final String FOEDSELSMELDING_URL = "/api/v1/personer/foedselsmelding";
    private static final String MILJOER = "miljoer";

    private final WebClient webClient;
    private final FoedselsmeldingRequest request;
    private final Set<String> miljoer;
    private final String token;

    @Override
    public Mono<FoedselsmeldingResponse> call() {

        return webClient
                .post()
                .uri(builder -> builder.path(FOEDSELSMELDING_URL)
                        .queryParam(MILJOER, miljoer)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(BodyInserters.fromValue(request))
                .retrieve()
                .bodyToMono(FoedselsmeldingResponse.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .doOnError(WebClientFilter::logErrorMessage);
    }
}
