package no.nav.registre.testnorge.helsepersonellservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.helsepersonellservice.exception.SamhandlerException;
import no.nav.registre.testnorge.helsepersonellservice.util.WebClientFilter;
import no.nav.testnav.libs.dto.samhandlerregisteret.v1.SamhandlerDTO;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.concurrent.Callable;

import static no.nav.registre.testnorge.helsepersonellservice.util.Headers.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
public class GetSamhandlerCommand implements Callable<Flux<SamhandlerDTO>> {

    private final String ident;
    private final WebClient webClient;
    private final String token;

    @Override
    public Flux<SamhandlerDTO> call() {
        log.info("Henter samhandlerinformasjon for ident {}", ident);
        return webClient
                .get()
                .uri(builder -> builder
                        .path("/sar/rest/v2/samh")
                        .queryParam("ident", ident)
                        .build())
                .header(AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToFlux(SamhandlerDTO.class)
                .onErrorResume(throwable -> {
                    log.error("Feil ved henting av samhandlerinformasjon til ident {}.", ident);
                    throw new SamhandlerException(WebClientFilter.getMessage(throwable));
                });
    }
}
