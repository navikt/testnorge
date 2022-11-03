package no.nav.testnav.apps.syntaaregservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntaaregservice.domain.synt.RsAaregSyntetiseringsRequest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import static no.nav.testnav.apps.syntaaregservice.domain.CommonKeys.RESPONSE_TYPE_LIST_AAREG_REQUEST;

@Slf4j
@RequiredArgsConstructor
public class PostSyntAaregCommand implements Callable<List<RsAaregSyntetiseringsRequest>> {

    private static final ParameterizedTypeReference<List<String>> REQUEST_TYPE = new ParameterizedTypeReference<>() {
    };
    private final List<String> fnrs;
    private final WebClient webClient;

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
                    .block();
        } catch (Exception e) {
            log.error("Feil under syntetisering", e);
            return Collections.emptyList();
        }
    }
}
