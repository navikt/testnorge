package no.nav.registre.testnorge.oppsummeringsdokumentservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
public class SaveOpplysningspliktigCommand implements Runnable {
    private final WebClient webClient;
    private final String xml;
    private final String token;
    private final String miljo;

    @SneakyThrows
    @Override
    public void run() {
        try {
            webClient
                    .post()
                    .uri(builder -> builder.path("/oppsummeringsdokument").queryParam("targetEnvironment", miljo).build())
                    .body(BodyInserters.fromPublisher(Mono.just(xml), String.class))
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } catch (
                WebClientResponseException e) {
            log.error("Feil ved lagring av oppsummeringsdokument: {}.", e.getResponseBodyAsString());
            log.error(xml);
            throw e;
        }
    }
}
