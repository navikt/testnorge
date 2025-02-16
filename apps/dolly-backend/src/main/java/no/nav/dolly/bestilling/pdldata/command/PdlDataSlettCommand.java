package no.nav.dolly.bestilling.pdldata.command;

import io.netty.handler.timeout.TimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.concurrent.Callable;

import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@Slf4j
@RequiredArgsConstructor
public class PdlDataSlettCommand implements Callable<Flux<Void>> {

    private static final String PDL_FORVALTER_URL = "/api/v1/personer/{ident}";

    private final WebClient webClient;
    private final String ident;
    private final String token;

    public Flux<Void> call() {

        return webClient
                .delete()
                .uri(PDL_FORVALTER_URL, ident)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .retrieve()
                .bodyToFlux(Void.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .onErrorMap(TimeoutException.class, e -> new HttpTimeoutException("Timeout on DELETE of ident %s".formatted(ident)))
                .doOnError(throwable -> !(throwable instanceof WebClientResponseException.NotFound), WebClientFilter::logErrorMessage)
                .onErrorResume(throwable -> throwable instanceof WebClientResponseException.NotFound,
                        throwable -> Flux.empty());
    }
}
