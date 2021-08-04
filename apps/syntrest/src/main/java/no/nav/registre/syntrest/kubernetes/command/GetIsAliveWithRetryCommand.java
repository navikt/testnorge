package no.nav.registre.syntrest.kubernetes.command;

import java.time.Duration;
import java.util.concurrent.Callable;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import no.nav.registre.syntrest.kubernetes.exception.KubernetesException;
import reactor.util.retry.Retry;

public class GetIsAliveWithRetryCommand implements Callable<String> {

    private final WebClient webClient;
    private final String isAliveUrl;
    private final String appName;
    private final int maxRetries;
    private final int retryDelay;

    public GetIsAliveWithRetryCommand(WebClient webClient, String isAliveUrl, String appName, int maxRetries, int retryDelay) {
        this.webClient = webClient;
        this.isAliveUrl = isAliveUrl;
        this.appName = appName;
        this.maxRetries = maxRetries;
        this.retryDelay = retryDelay;
    }

    @Override
    public String call() {
        return webClient.get()
                .uri(isAliveUrl.replace("{appName}", appName))
                .retrieve()
                .bodyToMono(String.class)
                .retryWhen(Retry.fixedDelay(maxRetries, Duration.ofSeconds(retryDelay))
                        .filter(e -> e instanceof WebClientResponseException.ServiceUnavailable ||
                                e instanceof WebClientResponseException.NotFound)
                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> new KubernetesException("Failed to poll application: too many retries.")))
                .block();
    }
}
