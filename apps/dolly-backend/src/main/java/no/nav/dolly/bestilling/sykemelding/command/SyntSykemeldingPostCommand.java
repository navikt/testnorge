package no.nav.dolly.bestilling.sykemelding.command;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.sykemelding.domain.SyntSykemeldingRequest;
import no.nav.dolly.bestilling.sykemelding.dto.SykemeldingResponse;
import no.nav.testnav.libs.dto.sykemelding.v1.SykemeldingResponseDTO;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@RequiredArgsConstructor
public class SyntSykemeldingPostCommand {

    private static final String SYNT_SYKEMELDING_URL = "/api/v1/synt-sykemelding";

    private final WebClient webClient;
    private final SyntSykemeldingRequest sykemeldingRequest;
    private final String token;

    public Mono<SykemeldingResponse> call() {

        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(SYNT_SYKEMELDING_URL)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .bodyValue(sykemeldingRequest)
                .retrieve()
                .bodyToMono(SykemeldingResponseDTO.class)
                .map(response -> SykemeldingResponse.builder()
                        .status(response.getStatus())
                        .msgId(response.getSykemeldingId())
                        .ident(sykemeldingRequest.getIdent())
                        .sykemeldingRequest(SykemeldingResponse.SykemeldingRequest.builder()
                                .syntSykemeldingRequest(sykemeldingRequest)
                                .build())
                        .build())
                .doOnError(WebClientFilter::logErrorMessage)
                .onErrorResume(error -> Mono.just(SykemeldingResponse.builder()
                        .ident(sykemeldingRequest.getIdent())
                        .status(WebClientFilter.getStatus(error))
                        .avvik(WebClientFilter.getMessage(error))
                        .build()))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
