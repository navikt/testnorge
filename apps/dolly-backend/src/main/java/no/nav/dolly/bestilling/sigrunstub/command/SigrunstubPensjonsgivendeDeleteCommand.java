package no.nav.dolly.bestilling.sigrunstub.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.sigrunstub.dto.SigrunstubResponse;
import no.nav.dolly.util.CallIdUtil;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
public class SigrunstubPensjonsgivendeDeleteCommand implements Callable<Mono<SigrunstubResponse>> {

    private static final String CONSUMER = "Dolly";
    private static final String SIGRUNSTUB_DELETE_URL = "/api/v1/pensjonsgivendeinntektforfolketrygden";

    private final WebClient webClient;
    private final String ident;
    private final String token;

    public Mono<SigrunstubResponse> call() {

        return webClient.delete().uri(uriBuilder -> uriBuilder
                        .path(SIGRUNSTUB_DELETE_URL)
                        .build())
                .header(HEADER_NAV_CALL_ID, CallIdUtil.generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header("norskident", ident)
                .header(AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .retrieve()
                .toBodilessEntity()
                .map(resultat -> SigrunstubResponse.builder()
                        .ident(ident)
                        .status(HttpStatus.valueOf(resultat.getStatusCode().value()))
                        .build())
                .doOnError(throwable -> !(throwable instanceof WebClientResponseException.NotFound), WebClientFilter::logErrorMessage)
                .onErrorResume(error -> Mono.just(SigrunstubResponse.builder()
                        .ident(ident)
                        .status(WebClientFilter.getStatus(error))
                        .melding(WebClientFilter.getMessage(error))
                        .build()))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
