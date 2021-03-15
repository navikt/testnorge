package no.nav.registre.testnorge.bridge.kafkaonprembridge.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import no.nav.registre.testnorge.libs.dto.bridge.v1.ContentDTO;

@RequiredArgsConstructor
public class BridgeKafkaCommand implements Runnable {
    private final WebClient webClient;
    private final ContentDTO content;
    private final String token;
    private final String path;

    @Override
    public void run() {
        webClient.post()
                .uri(path)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(BodyInserters.fromPublisher(Mono.just(content), ContentDTO.class))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
