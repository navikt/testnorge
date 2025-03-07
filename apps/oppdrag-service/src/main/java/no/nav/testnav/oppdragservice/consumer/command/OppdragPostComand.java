package no.nav.testnav.oppdragservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class OppdragPostComand implements Callable<Mono<String>> {

    private static final String OPPDRAG_URL = "/{miljoe}/cics/services/oppdragService";

    private final WebClient webClient;
    private final String token;
    private final String miljoe;
    private final String melding;

    @Override
    public Mono<String> call() {

        log.info("Sender melding til oppdrag {}", melding);

        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path(OPPDRAG_URL).build(miljoe))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
//                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
                .bodyValue(melding)
                .retrieve()
                .bodyToMono(String.class);
    }
}
