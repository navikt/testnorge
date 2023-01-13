package no.nav.dolly.bestilling.pdldata.command;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.pdldata.dto.PdlResponse;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@RequiredArgsConstructor
public class PdlDataOrdreCommand implements Callable<Flux<PdlResponse>> {

    private static final String PDL_FORVALTER_ORDRE_URL = "/api/v1/personer/{ident}/ordre";
    private static final String IS_TPS_MASTER = "isTpsMaster";
    private static final String EXCLUDE_EKSTERNE_PERSONER = "ekskluderEksternePersoner";

    private final WebClient webClient;
    private final String ident;
    private final boolean ekskluderEksternePersoner;
    private final String token;

    public Flux<PdlResponse> call() {

        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder.path(PDL_FORVALTER_ORDRE_URL)
                        .queryParam(IS_TPS_MASTER, false)
                        .queryParam(EXCLUDE_EKSTERNE_PERSONER, ekskluderEksternePersoner)
                        .build(ident))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(String.class)
                .map(reultat -> PdlResponse.builder()
                        .jsonNode(reultat)
                        .status(HttpStatus.OK)
                        .build())
                .doOnError(WebClientFilter::logErrorMessage)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .onErrorResume(throwable -> Flux.just(PdlResponse.builder()
                        .status(WebClientFilter.getStatus(throwable))
                        .feilmelding(WebClientFilter.getMessage(throwable))
                        .build()));
    }
}
