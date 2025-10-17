package no.nav.testnav.altinn3tilgangservice.consumer.brreg;

import no.nav.testnav.altinn3tilgangservice.consumer.altinn.dto.BrregResponseDTO;
import no.nav.testnav.altinn3tilgangservice.consumer.brreg.command.GetBrregEnheterCommand;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class BrregConsumer {

    private static final String BRREG_URL = "https://data.brreg.no";

    private final WebClient webClient;

    public BrregConsumer(WebClient webClient) {
        this.webClient = webClient
                .mutate()
                .baseUrl(BRREG_URL)
                .build();
    }

    public Mono<BrregResponseDTO> getEnhet(String orgnummer) {

        return new GetBrregEnheterCommand(webClient, orgnummer).call();
    }
}
