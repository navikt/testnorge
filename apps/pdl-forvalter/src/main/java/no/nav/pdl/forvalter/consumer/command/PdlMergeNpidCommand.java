package no.nav.pdl.forvalter.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.data.pdlforvalter.v1.OrdreResponseDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.PdlStatus;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.boot.web.server.WebServerException;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
public class PdlMergeNpidCommand extends PdlTestdataCommand {

    private final WebClient webClient;
    private final String url;
    private final String npid;
    private final String otherIdent;
    private final String token;

    @Override
    public Flux<OrdreResponseDTO.HendelseDTO> call() {
        return webClient
                .post()
                .uri(builder -> builder
                        .path(url)
                        .queryParam("npid", npid)
                        .queryParam("otherIdent", otherIdent)
                        .build())
                .headers(WebClientHeader.bearer(token))
                .contentType(MediaType.APPLICATION_JSON)
                .exchangeToFlux(response ->
                        Flux.just(OrdreResponseDTO.HendelseDTO.builder()
                                .status(PdlStatus.OK)
                                .build()))
                .timeout(TIMEOUT)
                .doOnError(WebServerException.class, error -> log.error(error.getMessage(), error))
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(error -> Mono.just(errorHandling(error, null)));
    }

}