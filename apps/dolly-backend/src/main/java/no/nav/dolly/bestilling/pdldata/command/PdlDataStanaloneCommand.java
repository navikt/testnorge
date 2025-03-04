package no.nav.dolly.bestilling.pdldata.command;

import io.netty.handler.timeout.TimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivecore.web.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.concurrent.Callable;

import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@Slf4j
@RequiredArgsConstructor
public class PdlDataStanaloneCommand implements Callable<Mono<String>> {

    private static final String PDL_FORVALTER_IDENTER_STANDALONE_URL = "/api/v1/identiteter/{ident}/standalone/{standalone}";

    private final WebClient webClient;
    private final String ident;
    private final Boolean standalone;
    private final String token;

    public Mono<String> call() {

        return webClient
                .put()
                .uri(uriBuilder -> uriBuilder.path(PDL_FORVALTER_IDENTER_STANDALONE_URL)
                        .build(ident, standalone))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .toBodilessEntity()
                .map(response -> "OK")
                .onErrorMap(TimeoutException.class, e -> new HttpTimeoutException("Timeout on PUT for ident %s".formatted(ident)))
                .doOnError(WebClientFilter::logErrorMessage)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
