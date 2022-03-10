package no.nav.registre.testnorge.organisasjonmottak.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.servletcore.util.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;

@RequiredArgsConstructor
public class RegisterEregBestillingCommand implements Runnable {
    private final WebClient webClient;
    private final String token;
    private final String uuid;
    private final String miljo;
    private final Long itemId;

    @Override
    public void run() {
        webClient
                .post()
                .uri(builder -> builder.path("/api/v1/ereg/batch/queue/items/{id}").build(itemId))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header("miljo", miljo)
                .header("uuid", uuid)
                .retrieve()
                .bodyToMono(Void.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .block();
    }
}
