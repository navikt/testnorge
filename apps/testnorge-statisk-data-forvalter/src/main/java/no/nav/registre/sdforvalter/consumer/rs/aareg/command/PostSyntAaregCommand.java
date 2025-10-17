package no.nav.registre.sdforvalter.consumer.rs.aareg.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.sdforvalter.consumer.rs.aareg.request.RsAaregSyntetiseringsRequest;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;


@Slf4j
@RequiredArgsConstructor
public class PostSyntAaregCommand implements Callable<List<RsAaregSyntetiseringsRequest>> {

    private static final ParameterizedTypeReference<List<String>> REQUEST_TYPE = new ParameterizedTypeReference<>() {
    };
    public static final ParameterizedTypeReference<List<RsAaregSyntetiseringsRequest>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };
    private final List<String> fnrs;
    private final WebClient webClient;

    @Override
    public List<RsAaregSyntetiseringsRequest> call() {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/generate_aareg")
                        .build())
                .body(Mono.just(fnrs), REQUEST_TYPE)
                .retrieve()
                .bodyToMono(RESPONSE_TYPE)
                .doOnError(WebClientError.logTo(log))
                .onErrorResume(error -> {
                    log.error("Feil under syntetisering");
                    return Mono.just(Collections.emptyList());
                })
                .block();
    }
}
