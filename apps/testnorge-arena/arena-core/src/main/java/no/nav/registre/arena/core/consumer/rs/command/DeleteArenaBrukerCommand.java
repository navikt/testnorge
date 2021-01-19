package no.nav.registre.arena.core.consumer.rs.command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.Callable;

import static no.nav.registre.arena.core.consumer.rs.util.Headers.CALL_ID;
import static no.nav.registre.arena.core.consumer.rs.util.Headers.CONSUMER_ID;
import static no.nav.registre.arena.core.consumer.rs.util.Headers.NAV_CALL_ID;
import static no.nav.registre.arena.core.consumer.rs.util.Headers.NAV_CONSUMER_ID;

@Slf4j
public class DeleteArenaBrukerCommand implements Callable<Boolean> {
    private final WebClient webClient;
    private final String arenaUrl;
    private final String personident;
    private final String miljoe;

    public DeleteArenaBrukerCommand(String personident,
                                    String miljoe, WebClient webClient) {
        this.webClient = webClient;
        this.arenaUrl = "/v1/bruker";
        this.personident = personident;
        this.miljoe = miljoe;
    }

    @Override
    public Boolean call() {
        var response = true;
        try {
            log.info("Sletter ident {} fra Arena Forvalter i miljø {}.", personident, miljoe);

            var statusCode = webClient.delete()
                    .uri(builder ->
                            builder.path(arenaUrl)
                                    .queryParam("miljoe", miljoe)
                                    .queryParam("personident", personident)
                                    .build()
                    )
                    .header(CALL_ID, NAV_CALL_ID)
                    .header(CONSUMER_ID, NAV_CONSUMER_ID)
                    .exchange()
                    .map(ClientResponse::statusCode)
                    .block();

            if (!statusCode.is2xxSuccessful()) {
                log.error("Kunne ikke slette ident {} fra Arena-forvalteren. Status: {}", personident, statusCode.toString());
                response = false;
            }

        } catch (Exception e) {
            log.error("Klarte ikke slette ident {} fra Arena-forvalteren.", personident, e);
            response = false;
        }
        return response;
    }
}
