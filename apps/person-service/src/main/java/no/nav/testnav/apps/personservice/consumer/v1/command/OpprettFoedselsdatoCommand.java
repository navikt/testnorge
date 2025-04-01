package no.nav.testnav.apps.personservice.consumer.v1.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.personservice.consumer.v1.header.PdlHeaders;
import no.nav.testnav.apps.personservice.consumer.v1.pdl.FoedselsdatoDTO;
import no.nav.testnav.apps.personservice.consumer.v1.pdl.HendelseDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class OpprettFoedselsdatoCommand implements Callable<Mono<HendelseDTO>> {

    private final WebClient webClient;
    private final FoedselsdatoDTO dto;
    private final String token;
    private final String ident;

    @Override
    public Mono<HendelseDTO> call() {
        return webClient
                .post()
                .uri("/pdl-testdata/api/v1/bestilling/foedselsdato")
                .accept(MediaType.APPLICATION_JSON)
                .header(PdlHeaders.NAV_PERSONIDENT, ident)
                .headers(WebClientHeader.bearer(token))
                .body(BodyInserters.fromPublisher(Mono.just(dto), FoedselsdatoDTO.class))
                .retrieve()
                .bodyToMono(HendelseDTO.class)
                .retryWhen(WebClientError.is5xxException());
    }

}
