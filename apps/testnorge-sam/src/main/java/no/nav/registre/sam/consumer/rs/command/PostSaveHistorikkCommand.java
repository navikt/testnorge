package no.nav.registre.sam.consumer.rs.command;

import lombok.RequiredArgsConstructor;
import no.nav.registre.sam.domain.SamSaveInHodejegerenRequest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class PostSaveHistorikkCommand implements Callable<List<String>> {

    private final SamSaveInHodejegerenRequest request;
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
                .body(BodyInserters.fromPublisher(Mono.just(request), SamSaveInHodejegerenRequest.class))
                .retrieve()
                .bodyToMono(RESPONSE_TYPE)
                .block();
    }
}
