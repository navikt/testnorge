package no.nav.dolly.bestilling.pdldata.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@Slf4j
@RequiredArgsConstructor
public class PdlDataOrdreCommand implements Callable<Mono<String>> {

    private static final String PDL_FORVALTER_ORDRE_URL = "/api/v1/personer/{ident}/ordre";
    private static final String IS_TPS_MASTER = "isTpsMaster";
    private static final String EXCLUDE_EKSTERNE_PERSONER = "ekskluderEksternePersoner";

    private final WebClient webClient;
    private final String ident;
    private final boolean isTpsfMaster;
    private final boolean ekskluderEksternePersoner;
    private final String token;

    public Mono<String> call() {

        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder.path(PDL_FORVALTER_ORDRE_URL)
                        .queryParam(IS_TPS_MASTER, isTpsfMaster)
                        .queryParam(EXCLUDE_EKSTERNE_PERSONER, ekskluderEksternePersoner)
                        .build(ident))
                .header(HttpHeaders.AUTHORIZATION, token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(WebClientFilter::logErrorMessage)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .onErrorResume(throwable -> throwable instanceof WebClientResponseException.NotFound,
                        throwable -> Mono.just("Ident ikke funnet"));
    }
}
