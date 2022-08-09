package no.nav.testnav.apps.instservice.consumer.command;


import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import no.nav.testnav.apps.instservice.provider.responses.SupportedEnvironmentsResponse;
import no.nav.testnav.apps.instservice.util.WebClientFilter;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static no.nav.testnav.apps.instservice.properties.HttpRequestConstants.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
public class GetTilgjengeligeMiljoerCommand implements Callable<Mono<SupportedEnvironmentsResponse>> {
    private final WebClient webClient;
    private final String token;

    @SneakyThrows
    @Override
    public Mono<SupportedEnvironmentsResponse> call() {
        try {
            return webClient.get()
                    .uri(builder ->
                            builder.path("/api/v1/environment")
                                    .build()
                    )
                    .header(AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .bodyToMono(SupportedEnvironmentsResponse.class)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                            .filter(WebClientFilter::is5xxException));
        } catch (WebClientResponseException e) {
            log.error("Henting av tilgjengelige miljøer i Inst2 feilet: ", e);
            return Mono.just(new SupportedEnvironmentsResponse());
        }
    }
}
