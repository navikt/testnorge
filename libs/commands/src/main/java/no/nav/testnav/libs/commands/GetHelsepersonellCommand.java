package no.nav.testnav.libs.commands;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import no.nav.testnav.libs.commands.utils.WebClientFilter;
import no.nav.testnav.libs.dto.helsepersonell.v1.HelsepersonellListeDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetHelsepersonellCommand implements Callable<HelsepersonellListeDTO> {
    private final WebClient webClient;
    private final String accessToken;

    @SneakyThrows
    @Override
    public HelsepersonellListeDTO call() {
        return webClient
                .get()
                .uri("/api/v1/helsepersonell")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(HelsepersonellListeDTO.class)
                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(3)))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .block();
    }
}