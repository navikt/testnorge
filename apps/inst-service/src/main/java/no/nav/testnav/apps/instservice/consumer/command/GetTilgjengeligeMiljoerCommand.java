package no.nav.testnav.apps.instservice.consumer.command;


import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import no.nav.testnav.apps.instservice.provider.responses.SupportedEnvironmentsResponse;
import no.nav.testnav.apps.instservice.util.WebClientFilter;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class GetTilgjengeligeMiljoerCommand implements Callable<SupportedEnvironmentsResponse> {
    private final WebClient webClient;

    @SneakyThrows
    @Override
    public SupportedEnvironmentsResponse call() {
        try {
            return webClient.get()
                    .uri(builder ->
                            builder.path("/v1/environment")
                                    .build()
                    )
                    .retrieve()
                    .bodyToMono(SupportedEnvironmentsResponse.class)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                            .filter(WebClientFilter::is5xxException))
                    .block();
        } catch (WebClientResponseException e) {
            log.error("Henting av tilgjengelige miljøer i Inst2 feilet: ", e);
            return new SupportedEnvironmentsResponse();
        }
    }
}
