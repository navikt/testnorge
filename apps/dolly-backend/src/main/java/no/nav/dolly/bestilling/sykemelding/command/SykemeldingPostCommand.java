package no.nav.dolly.bestilling.sykemelding.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.sykemelding.domain.DetaljertSykemeldingRequest;
import no.nav.dolly.bestilling.sykemelding.dto.SykemeldingResponse;
import no.nav.testnav.libs.dto.sykemelding.v1.SykemeldingResponseDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@Slf4j
@RequiredArgsConstructor
public class SykemeldingPostCommand implements Callable<Mono<SykemeldingResponse>> {

    private static final String DETALJERT_SYKEMELDING_URL = "/api/v1/sykemeldinger";

    private final WebClient webClient;
    private final DetaljertSykemeldingRequest request;
    private final String token;

    @Override
    public Mono<SykemeldingResponse> call() {

        return webClient.post().uri(uriBuilder -> uriBuilder
                        .path(DETALJERT_SYKEMELDING_URL)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
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
                .doOnError(WebClientFilter::logErrorMessage)
                .onErrorResume(error -> Mono.just(SykemeldingResponse.builder()
                        .ident(request.getPasient().getIdent())
                        .status(WebClientFilter.getStatus(error))
                        .avvik(WebClientFilter.getMessage(error))
                        .build()))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
