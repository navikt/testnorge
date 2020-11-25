package no.nav.registre.testnorge.person.consumer.command;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.Callable;

import no.nav.registre.testnorge.person.consumer.dto.pdl.OpprettPersonDTO;
import no.nav.registre.testnorge.person.consumer.header.PdlHeaders;

@RequiredArgsConstructor
public class PostOpprettPersonCommand implements Callable<OpprettPersonDTO> {
    private final WebClient webClient;
    private final String ident;
    private final String kilde;
    private final String token;
    private final String url;


    @Override
    public OpprettPersonDTO call() {
        var a  = WebClient.builder().baseUrl(url).build();
        return a.post()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/bestilling/opprettperson").build())
                .accept(MediaType.APPLICATION_JSON)
                .header(PdlHeaders.NAV_PERSONIDENT, ident)
                .header(PdlHeaders.KILDE, kilde)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(OpprettPersonDTO.class)
                .block();
    }
}
