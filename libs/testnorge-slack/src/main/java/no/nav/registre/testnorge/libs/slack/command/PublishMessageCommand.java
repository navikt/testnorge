package no.nav.registre.testnorge.libs.slack.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import no.nav.registre.testnorge.libs.slack.dto.Message;
import no.nav.registre.testnorge.libs.slack.dto.SlackResponse;

@RequiredArgsConstructor
public class PublishMessageCommand implements Callable<SlackResponse> {
    private final WebClient webClient;
    private final String token;
    private final Message message;

    @Override
    public SlackResponse call() {
        return webClient
                .post()
                .uri("/api/chat.postMessage")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromPublisher(Mono.just(message), Message.class))
                .retrieve()
                .bodyToMono(SlackResponse.class)
                .block();
    }
}