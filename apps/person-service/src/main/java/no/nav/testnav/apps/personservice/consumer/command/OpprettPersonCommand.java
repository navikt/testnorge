package no.nav.testnav.apps.personservice.consumer.command;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.Callable;

import no.nav.testnav.apps.personservice.consumer.dto.pdl.OpprettPersonDTO;
import no.nav.testnav.apps.personservice.consumer.header.PdlHeaders;

@RequiredArgsConstructor
public class OpprettPersonCommand implements Callable<OpprettPersonDTO> {
    private final WebClient webClient;
    private final String ident;
    private final String kilde;
    private final String token;

    @Override
    public OpprettPersonDTO call() {
        return webClient.post()
                .uri("/api/v1/bestilling/opprettperson")
                .accept(MediaType.APPLICATION_JSON)
                .header(PdlHeaders.NAV_PERSONIDENT, ident)
                .header(PdlHeaders.KILDE, kilde)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(OpprettPersonDTO.class)
                .block();
    }
}
