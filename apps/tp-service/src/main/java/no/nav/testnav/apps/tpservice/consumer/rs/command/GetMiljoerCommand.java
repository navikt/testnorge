package no.nav.testnav.apps.tpservice.consumer.rs.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.concurrent.Callable;

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
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
//                .header(HEADER_NAV_CALL_ID, generateCallId())
//                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Set<String>>() {
                })
                .onErrorResume(throwable -> Mono.empty());
    }
}
