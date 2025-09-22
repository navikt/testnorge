package no.nav.dolly.bestilling.sykemelding.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.sykemelding.domain.DetaljertSykemeldingRequest;
import no.nav.dolly.bestilling.sykemelding.dto.SykemeldingResponse;
import no.nav.testnav.libs.dto.sykemelding.v1.SykemeldingResponseDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class SyfosmreglerSykemeldingPostCommand implements Callable<Mono<SykemeldingResponse>> {

    private static final String DETALJERT_SYKEMELDING_URL = "/api/v1/sykemeldinger";

    private final WebClient webClient;
    private final DetaljertSykemeldingRequest request;
    private final String token;

    @Override
    public Mono<SykemeldingResponse> call() {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder.path(DETALJERT_SYKEMELDING_URL).build())
                .headers(WebClientHeader.bearer(token))
                .bodyValue(request)
                .retrieve()
                .bodyToMono(SykemeldingResponseDTO.class)
                .map(response -> SykemeldingResponse.builder()
                        .status(response.getStatus())
                        .msgId(response.getSykemeldingId())
                        .ident(request.getPasient().getIdent())
                        .sykemeldingRequest(SykemeldingResponse.SykemeldingRequest.builder()
                                .detaljertSykemeldingRequest(request)
                                .build())
                        .build())
                .doOnError(WebClientError.logTo(log))
                .onErrorResume(error -> SykemeldingResponse.of(WebClientError.describe(error), request.getPasient().getIdent()));
    }
}