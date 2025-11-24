package no.nav.testnav.apps.personservice.consumer.v1.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.personservice.consumer.v1.header.PdlHeaders;
import no.nav.testnav.apps.personservice.consumer.v1.pdl.OpprettPersonDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class OpprettPersonCommand implements Callable<Mono<OpprettPersonDTO>> {

    private final WebClient webClient;
    private final String ident;
    private final String kilde;
    private final String token;

    @Override
    public Mono<OpprettPersonDTO> call() {
        return webClient
                .post()
                .uri("/pdl-testdata/api/v1/bestilling/opprettperson")
                .accept(MediaType.APPLICATION_JSON)
                .header(PdlHeaders.NAV_PERSONIDENT, ident)
                .header(PdlHeaders.KILDE, kilde)
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToMono(OpprettPersonDTO.class)
                .retryWhen(WebClientError.is5xxException());
    }

}
