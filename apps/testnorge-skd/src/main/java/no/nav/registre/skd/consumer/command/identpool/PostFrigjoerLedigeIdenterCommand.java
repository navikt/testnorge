package no.nav.registre.skd.consumer.command.identpool;

import java.util.List;
import java.util.concurrent.Callable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class PostFrigjoerLedigeIdenterCommand implements Callable<List<String>> {

    private final List<String> identer;
    private final WebClient webClient;

    private static final ParameterizedTypeReference<List<String>> LIST_TYPE = new ParameterizedTypeReference<>() {
    };

    @Override
    public List<String> call() {
        return webClient.post()
                .uri(builder ->
                        builder.path("/v1/identifikator/frigjoerLedige")
                                .build()
                )
                .body(BodyInserters.fromPublisher(Mono.just(identer), LIST_TYPE))
                .retrieve()
                .bodyToMono(LIST_TYPE)
                .block();
    }
}
