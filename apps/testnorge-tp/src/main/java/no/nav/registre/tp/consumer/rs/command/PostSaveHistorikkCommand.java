package no.nav.registre.tp.consumer.rs.command;

import lombok.RequiredArgsConstructor;
import no.nav.registre.tp.domain.TpSaveInHodejegerenRequest;
import no.nav.testnav.libs.servletcore.util.WebClientFilter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class PostSaveHistorikkCommand implements Callable<List<String>> {

    private final TpSaveInHodejegerenRequest request;
    private final WebClient webClient;

    private static final ParameterizedTypeReference<List<String>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    @Override
    public List<String> call() {

        return webClient.post()
                .uri(builder ->
                        builder.path("/v1/historikk")
                                .build()
                )
                .body(BodyInserters.fromPublisher(Mono.just(request), TpSaveInHodejegerenRequest.class))
                .retrieve()
                .bodyToMono(RESPONSE_TYPE)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .block();
    }
}