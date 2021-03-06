package no.nav.registre.testnorge.arena.consumer.rs.command.tpsf;

import java.util.concurrent.Callable;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.arena.consumer.rs.response.tpsf.PersonstatusOgAdresse;

@Slf4j
public class GetPersonstatusOgAdresseCommand implements Callable<PersonstatusOgAdresse> {

    private final WebClient webClient;
    private final String ident;
    private final String miljoe;

    public GetPersonstatusOgAdresseCommand(String ident, String miljoe, WebClient webClient) {
        this.webClient = webClient;
        this.ident = ident;
        this.miljoe = miljoe;
    }

    @Override
    public PersonstatusOgAdresse call() {
        try {
            log.info("Henter personstatus og adresse for ident.");
            return webClient.get()
                    .uri(builder ->
                            builder.path("/tps/personstatus-og-adresse-person")
                                    .queryParam("fnr", ident)
                                    .queryParam("environment", miljoe)
                                    .queryParam("aksjonsKode", "A0")
                                    .build()
                    )
                    .retrieve()
                    .bodyToMono(PersonstatusOgAdresse.class)
                    .block();
        } catch (Exception e) {
            log.error("Klarte ikke å hente personstatus og adresse i TPS for ident: " + ident, e);
            return null;
        }
    }
}
