package no.nav.registre.skd.consumer.command.hodejegeren;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.commands.utils.WebClientFilter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class HodejegerenStatusQuoCommand implements Callable<Map<String, String>> {
    private static final ParameterizedTypeReference<Map<String, String>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };
    private final WebClient webClient;
    private final String ident;
    private final String endringskode;
    private final String miljoe;

    @Override
    public Map<String, String> call() {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/status-quo")
                        .queryParam("endringskode", endringskode)
                        .queryParam("miljoe", miljoe)
                        .queryParam("fnr", ident)
                        .build())
                .retrieve()
                .bodyToMono(RESPONSE_TYPE)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .block();
    }
}
