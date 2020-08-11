package no.nav.registre.testnorge.helsepersonell.consumer.command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.Callable;

import no.nav.registre.testnorge.dto.hodejegeren.v1.PersondataDTO;

@Slf4j
public class GetPersondataCommand implements Callable<PersondataDTO> {

    private final String miljoe;
    private final String ident;
    private final WebClient webClient;

    public GetPersondataCommand(String ident, String miljoe, WebClient webClient) {
        this.webClient = webClient;
        this.miljoe = miljoe;
        this.ident = ident;
    }

    @Override
    public PersondataDTO call() {
        try {
            log.info("Henter persondata fra hodejegerern for ident {} i miljø {}", ident, miljoe);
            return webClient.get()
                    .uri(builder ->
                            builder.path("/v1/persondata")
                                    .queryParam("ident", ident)
                                    .queryParam("miljoe", miljoe)
                                    .build()
                    )
                    .retrieve()
                    .bodyToMono(PersondataDTO.class)
                    .block();
        } catch (Exception e) {
            log.error("Klarte ikke hente ut persondata fra hodejegerern for ident {} i miljø {}", ident, miljoe, e);
            throw e;
        }
    }
}
