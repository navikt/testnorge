package no.nav.dolly.bestilling.tpsmessagingservice.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.TpsMeldingResponseDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

import static java.util.Objects.nonNull;

@RequiredArgsConstructor
@Slf4j
public class TpsMessagingPostCommand implements Callable<Flux<TpsMeldingResponseDTO>> {

    private static final String MILJOER_PARAM = "miljoer";

    private final WebClient webClient;
    private final String ident;
    private final List<String> miljoer;
    private final Object body;
    private final String urlPath;
    private final String token;

    @Override
    public Flux<TpsMeldingResponseDTO> call() {
        log.info("Sender request på ident: {} til TPS messaging service: {}", ident, body);
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(urlPath)
                        .queryParamIfPresent(MILJOER_PARAM, nonNull(miljoer) ? Optional.of(miljoer) : Optional.empty())
                        .build(ident))
                .contentType(MediaType.APPLICATION_JSON)
                .headers(WebClientHeader.bearer(token))
                .bodyValue(body)
                .retrieve()
                .bodyToFlux(TpsMeldingResponseDTO.class)
                .doOnError(WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(throwable -> Mono.just(TpsMeldingResponseDTO
                        .builder()
                        .status("FEIL")
                        .utfyllendeMelding(WebClientError.describe(throwable).getMessage())
                        .build()));
    }

}
