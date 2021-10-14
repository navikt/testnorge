package no.nav.registre.sigrun.consumer.rs.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.sigrun.domain.PoppSyntetisererenResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class PostSyntPoppMeldingerCommand implements Callable<List<PoppSyntetisererenResponse>> {

    private final List<String> fnrs;
    private final String token;
    private final WebClient webClient;

    private static final ParameterizedTypeReference<List<PoppSyntetisererenResponse>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };
    private static final ParameterizedTypeReference<List<String>> REQUEST_TYPE = new ParameterizedTypeReference<>() {
    };

    @Override
    public List<PoppSyntetisererenResponse> call() {
        return webClient.post()
                .uri(builder ->
                        builder.path("/api/v1/generate/popp")
                                .build()
                )
                .header("Authorization", "Bearer " + token)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromPublisher(Mono.just(fnrs), REQUEST_TYPE))
                .retrieve()
                .bodyToMono(RESPONSE_TYPE)
                .block();
    }
}
