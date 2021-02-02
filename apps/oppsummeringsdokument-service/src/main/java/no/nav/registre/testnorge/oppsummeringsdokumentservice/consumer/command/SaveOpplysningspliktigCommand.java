package no.nav.registre.testnorge.oppsummeringsdokumentservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
public class SaveOpplysningspliktigCommand implements Runnable {
    private final WebClient webClient;
    private final String xml;
    private final String token;

    @SneakyThrows
    @Override
    public void run() {
        log.trace(xml);

        webClient
                .post()
                .uri(builder -> builder.path("/oppsummeringsdokument").build())
                .body(BodyInserters.fromPublisher(Mono.just(xml), String.class))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
