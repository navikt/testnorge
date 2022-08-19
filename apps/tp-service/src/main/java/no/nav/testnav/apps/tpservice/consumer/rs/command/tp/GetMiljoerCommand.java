package no.nav.testnav.apps.tpservice.consumer.rs.command.tp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.concurrent.Callable;

import static no.nav.testnav.apps.tpservice.util.CallIdUtil.generateCallId;
import static no.nav.testnav.apps.tpservice.util.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.testnav.apps.tpservice.util.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.testnav.apps.tpservice.util.CommonKeysAndUtils.CONSUMER;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
public class GetMiljoerCommand implements Callable<Mono<Set<String>>> {

    private final WebClient webClient;
    private final String token;

    public Mono<Set<String>> call() {

        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/miljo")
                        .build())
                .header(AUTHORIZATION, "Bearer " + token)
                .header(HEADER_NAV_CALL_ID, generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Set<String>>() {
                })
                .onErrorResume(throwable -> {
                    log.error(throwable.getMessage(), throwable);
                    return Mono.empty();
                });
    }
}
