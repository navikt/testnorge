package no.nav.registre.syntrest.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.registre.syntrest.domain.inntekt.Inntektsmelding;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class PostSyntInntektCommand implements Callable<Map<String, List<Inntektsmelding>>> {

    private final Map<String, List<Inntektsmelding>> fnrInntektMap;
    private final WebClient webClient;

    private static final ParameterizedTypeReference<Map<String, List<Inntektsmelding>>> INNTEKTSMELDING_MAP_TYPE = new ParameterizedTypeReference<>() {
    };

    @Override
    public Map<String, List<Inntektsmelding>> call() {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/generate/inntekt")
                        .build())
                .body(Mono.just(fnrInntektMap), INNTEKTSMELDING_MAP_TYPE)
                .retrieve()
                .bodyToMono(INNTEKTSMELDING_MAP_TYPE)
                .block();
    }
}
