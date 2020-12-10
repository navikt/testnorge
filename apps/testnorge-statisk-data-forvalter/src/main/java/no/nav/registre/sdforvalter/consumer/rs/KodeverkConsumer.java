package no.nav.registre.sdforvalter.consumer.rs;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.registre.sdforvalter.consumer.rs.commnad.GetNaeringskodeKodeverkCommand;
import no.nav.registre.sdforvalter.consumer.rs.dto.KodeverkDTO;
import no.nav.registre.sdforvalter.domain.Kodeverk;

@Component
public class KodeverkConsumer {
    private final WebClient webClient;

    public KodeverkConsumer(
            @Value("${consumers.kodeverk.url}") String url
    ) {
        this.webClient = WebClient
                .builder()
                .baseUrl(url)
                .build();
    }

    public Kodeverk getKodeverk(){
        KodeverkDTO dto = new GetNaeringskodeKodeverkCommand(webClient).call();
        return new Kodeverk(dto);
    }
}
