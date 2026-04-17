package no.nav.dolly.bestilling.sykemelding.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.sykemelding.domain.dto.SykemeldingRequestDTO;
import no.nav.dolly.bestilling.sykemelding.domain.dto.SykemeldingResponseDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class SykemeldingPostCommand implements Callable<Mono<SykemeldingResponseDTO>> {

    private static final String TSM_SYKEMELDING_URL = "/tsm/api/sykmelding";

    private final WebClient webClient;
    private final SykemeldingRequestDTO request;
    private final String token;

    @Override
    public Mono<SykemeldingResponseDTO> call() {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder.path(TSM_SYKEMELDING_URL).build())
                .headers(WebClientHeader.bearer(token))
                .bodyValue(request)
                .retrieve()
                .toEntity(SykemeldingResponseDTO.class)
                .map(response -> SykemeldingResponseDTO.builder()
                        .status(HttpStatus.resolve(response.getStatusCode().value()))
                        .sykemeldingRequest(request)
                        .ident(request.getIdent())
                        .build())
                .doOnError(WebClientError.logTo(log))
                .onErrorResume(error -> SykemeldingResponseDTO.of(WebClientError.describe(error), request.getIdent()));

    }
}