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
    private final String personident;
    private final String miljoe;

    public DeleteArenaBrukerCommand(String personident,
                                    String miljoe, WebClient webClient) {
        this.webClient = webClient;
        this.personident = personident;
        this.miljoe = miljoe;
    }

    @Override
    public Boolean call() {
        try {
            log.info("Sletter ident {} fra Arena Forvalter i miljø {}.", personident, miljoe);

            var statusCode = webClient.delete()
                    .uri(builder ->
                            builder.path("/v1/bruker")
                                    .queryParam("miljoe", miljoe)
                                    .queryParam("personident", personident)
                                    .build()
                    )
                    .header(CALL_ID, NAV_CALL_ID)
                    .header(CONSUMER_ID, NAV_CONSUMER_ID)
                    .exchange()
                    .map(ClientResponse::statusCode)
                    .block();

            assert statusCode != null;
            if (!statusCode.is2xxSuccessful()) {
                log.error("Kunne ikke slette ident {} fra Arena-forvalteren. Status: {}", personident, statusCode.toString());
                return false;
            }

        } catch (Exception | AssertionError e) {
            log.error("Klarte ikke slette ident {} fra Arena-forvalteren.", personident, e);
            return false;
        }
        return true;
    }
}
