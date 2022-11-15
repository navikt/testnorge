package no.nav.testnav.apps.instservice.util;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CheckAliveUtil {
    public static Map<String, String> checkConsumerStatus(String aliveUrl, String readyUrl, WebClient webClient) {
        ConcurrentHashMap<String, String> status = new ConcurrentHashMap();

        Thread blockingThread = new Thread(() -> {
            status.put("alive", checkStatus(webClient, aliveUrl).block());
            status.put("ready", checkStatus(webClient, readyUrl).block());
        });
        blockingThread.start();
        try {
            blockingThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return status;
    }

    private static Mono<String> checkStatus(WebClient webClient, String url) {
        return webClient.get().uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .defaultIfEmpty("OK")
                .onErrorResume(Exception.class, error -> Mono.just("Error: " + error.getMessage()))
                .map(result -> result.startsWith("Error:") ? result : "OK");
    }
}
