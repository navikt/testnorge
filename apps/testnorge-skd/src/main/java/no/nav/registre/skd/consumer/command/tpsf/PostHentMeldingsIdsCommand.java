package no.nav.registre.skd.consumer.command.tpsf;

import java.util.List;
import java.util.concurrent.Callable;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class PostHentMeldingsIdsCommand implements Callable<List<Long>> {

    private final Long avspillergruppeId;
    private final List<String> identer;
    private final WebClient webClient;

    private static final ParameterizedTypeReference<List<String>> REQUEST_TYPE = new ParameterizedTypeReference<>() {
    };
    private static final ParameterizedTypeReference<List<Long>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    @Override
    public List<Long> call() {
        return webClient.post()
                .uri(builder ->
                        builder.path("/v1/endringsmelding/skd/meldinger/{avspillergruppeId}")
                                .build(avspillergruppeId)
                )
                .body(BodyInserters.fromPublisher(Mono.just(identer), REQUEST_TYPE))
                .retrieve()
                .bodyToMono(RESPONSE_TYPE)
                .block();
    }
}
