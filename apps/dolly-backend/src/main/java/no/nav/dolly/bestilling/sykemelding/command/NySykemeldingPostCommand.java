package no.nav.dolly.bestilling.sykemelding.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.sykemelding.domain.dto.NySykemeldingRequestDTO;
import no.nav.dolly.bestilling.sykemelding.domain.dto.NySykemeldingResponseDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class NySykemeldingPostCommand implements Callable<Mono<NySykemeldingResponseDTO>> {

    private static final String TSM_SYKEMELDING_URL = "/tsm/api/sykmelding";

    private final WebClient webClient;
    private final NySykemeldingRequestDTO request;
    private final String token;

    @Override
    public Mono<NySykemeldingResponseDTO> call() {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder.path(TSM_SYKEMELDING_URL).build())
                .headers(WebClientHeader.bearer(token))
                .bodyValue(request)
                .retrieve()
                .bodyToMono(NySykemeldingResponseDTO.class)
                .map(response -> NySykemeldingResponseDTO.builder()
                        .status(response.getStatus())
                        .ident(request.getIdent())
                        .build())
                .doOnError(WebClientError.logTo(log))
                .onErrorResume(error -> NySykemeldingResponseDTO.of(WebClientError.describe(error), request.getIdent()));
    }
}