package no.nav.dolly.bestilling.sykemelding.command;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.sykemelding.dto.Norg2EnhetResponse;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@RequiredArgsConstructor
public class Norg2GetCommand implements Callable<Mono<Norg2EnhetResponse>> {

    private static final String NAVKONTOR_URL = "/api/v1/enhet/navkontor/{geografiskOmraade}";

    private final WebClient webClient;
    private final String geografiskOmraade;
    private final String token;

    @Override
    public Mono<Norg2EnhetResponse> call() {

        return webClient.get().uri(uriBuilder -> uriBuilder
                        .path(NAVKONTOR_URL)
                        .build(geografiskOmraade))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .retrieve()
                .bodyToMono(Norg2EnhetResponse.class)
                .doOnError(WebClientFilter::logErrorMessage)
                .onErrorResume(error -> Mono.just(Norg2EnhetResponse.builder()
                        .httpStatus(WebClientFilter.getStatus(error))
                        .avvik(WebClientFilter.getMessage(error))
                        .build()))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
