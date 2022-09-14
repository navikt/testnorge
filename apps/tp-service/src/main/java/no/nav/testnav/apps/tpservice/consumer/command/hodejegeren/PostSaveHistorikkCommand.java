package no.nav.testnav.apps.tpservice.consumer.command.hodejegeren;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.tpservice.domain.TpSaveInHodejegerenRequest;
import no.nav.testnav.apps.tpservice.util.WebClientFilter;
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

    private static final ParameterizedTypeReference<List<String>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };
    private final TpSaveInHodejegerenRequest request;
    private final WebClient webClient;

    @Override
    public List<String> call() {
        return webClient.post()
                .uri(builder ->
                        builder.path("/api/v1/historikk")
                                .build()
                )
                .body(BodyInserters.fromPublisher(Mono.just(request), TpSaveInHodejegerenRequest.class))
                .retrieve()
                .bodyToMono(RESPONSE_TYPE)
                .block();
    }
}