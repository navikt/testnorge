package no.nav.registre.aareg.consumer.rs.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.aareg.syntetisering.RsAaregSyntetiseringsRequest;
import no.nav.testnav.libs.servletcore.util.WebClientFilter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import static no.nav.registre.aareg.domain.CommonKeys.RESPONSE_TYPE_LIST_AAREG_REQUEST;

@Slf4j
@RequiredArgsConstructor
public class PostSyntAaregCommand implements Callable<List<RsAaregSyntetiseringsRequest>> {

    private final List<String> fnrs;
    private final WebClient webClient;

    private static final ParameterizedTypeReference<List<String>> REQUEST_TYPE = new ParameterizedTypeReference<>() {
    };

    @Override
    public List<RsAaregSyntetiseringsRequest> call() {
        try {
            return webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/generate_aareg")
                            .build())
                    .body(Mono.just(fnrs), REQUEST_TYPE)
                    .retrieve()
                    .bodyToMono(RESPONSE_TYPE_LIST_AAREG_REQUEST)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                            .filter(WebClientFilter::is5xxException))
                    .block();
        } catch (Exception e){
            log.error("Feil under syntetisering", e);
            return Collections.emptyList();
        }
    }
}
