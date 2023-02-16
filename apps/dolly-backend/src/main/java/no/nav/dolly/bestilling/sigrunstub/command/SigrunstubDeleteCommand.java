package no.nav.dolly.bestilling.sigrunstub.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.sigrunstub.dto.SigrunstubResponse;
import no.nav.dolly.util.CallIdUtil;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.web.reactive.function.client.WebClient;
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
public class SigrunstubDeleteCommand implements Callable<Mono<SigrunstubResponse>> {

    private static final String CONSUMER = "Dolly";
    private static final String SIGRUNSTUB_DELETE_URL = "/api/v1/slett";

    private final WebClient webClient;
    private final String ident;
    private final String token;

    public Mono<SigrunstubResponse> call() {

        return webClient.delete().uri(uriBuilder -> uriBuilder
                        .path(SIGRUNSTUB_DELETE_URL)
                        .build())
                .header(HEADER_NAV_CALL_ID, CallIdUtil.generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header(AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .header("personidentifikator", ident)
                .retrieve()
                .toBodilessEntity()
                .map(resultat -> SigrunstubResponse.builder()
                        .ident(ident)
                        .status(resultat.getStatusCode())
                        .build())
                .doOnError(WebClientFilter::logErrorMessage)
                .onErrorResume(error -> Mono.just(SigrunstubResponse.builder()
                                .ident(ident)
                                .status(WebClientFilter.getStatus(error))
                                .melding(WebClientFilter.getMessage(error))
                        .build()))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
