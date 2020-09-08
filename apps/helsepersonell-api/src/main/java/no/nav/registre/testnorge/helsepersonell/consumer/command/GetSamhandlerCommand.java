package no.nav.registre.testnorge.helsepersonell.consumer.command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.Callable;

import no.nav.registre.testnorge.libs.dto.samhandlerregisteret.v1.SamhandlerDTO;
import no.nav.registre.testnorge.helsepersonell.exception.IdentNotFoundException;

@Slf4j
public class GetSamhandlerCommand implements Callable<SamhandlerDTO[]> {
    private final String ident;
    private final WebClient webClient;

    public GetSamhandlerCommand(String ident, WebClient webClient) {
        this.ident = ident;
        this.webClient = webClient;
    }

    @Override
    public SamhandlerDTO[] call() {
        try {
            log.info("Henter samhandlerinformasjon for ident {}", ident);
            SamhandlerDTO[] response = webClient.get().uri(builder -> builder
                    .queryParam("ident", ident)
                    .build()
            ).retrieve().bodyToMono(SamhandlerDTO[].class).block();

            if (response == null || response.length == 0) {
                throw new IdentNotFoundException("Finer ikke ident " + ident + " i samhandlerregisteret.");
            }
            return response;
        } catch (Exception e) {
            log.error("Feil ved henting av samhandlerinformasjon til ident {}", ident, e);
            throw e;
        }
    }
}
