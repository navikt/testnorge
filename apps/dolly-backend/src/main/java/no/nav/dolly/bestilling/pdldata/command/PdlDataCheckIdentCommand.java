package no.nav.dolly.bestilling.pdldata.command;

import io.netty.handler.timeout.TimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.data.pdlforvalter.v1.AvailibilityResponseDTO;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;

import static java.lang.String.join;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@Slf4j
@RequiredArgsConstructor
public class PdlDataCheckIdentCommand implements Callable<Flux<AvailibilityResponseDTO>> {

    private static final String PDL_FORVALTER_EKSISTENS_URL = "/api/v1/eksistens";

    private final WebClient webClient;
    private final List<String> identer;
    private final String token;

    public Flux<AvailibilityResponseDTO> call() {

        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(PDL_FORVALTER_EKSISTENS_URL)
                        .queryParam("identer", identer)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .retrieve()
                .bodyToFlux(AvailibilityResponseDTO.class)
                .onErrorMap(TimeoutException.class, e -> new HttpTimeoutException("Timeout on GET of idents %s".formatted(join(",", identer))))
                .doOnError(WebClientFilter::logErrorMessage)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .onErrorResume(throwable -> throwable instanceof WebClientResponseException.NotFound,
                        throwable -> Mono.empty());
    }
}