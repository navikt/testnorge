package no.nav.dolly.bestilling.sigrunstub.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.sigrunstub.dto.SigrunstubResponse;
import no.nav.dolly.util.CallIdUtil;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;

@RequiredArgsConstructor
@Slf4j
public class SigrunstubPensjonsgivendeDeleteCommand implements Callable<Mono<SigrunstubResponse>> {

    private static final String SIGRUNSTUB_DELETE_URL = "/sigrunstub/api/v1/pensjonsgivendeinntektforfolketrygden";

    private final WebClient webClient;
    private final String ident;
    private final String token;

    @Override
    public Mono<SigrunstubResponse> call() {
        return webClient
                .delete()
                .uri(uriBuilder -> uriBuilder.path(SIGRUNSTUB_DELETE_URL).build())
                .header(HEADER_NAV_CALL_ID, CallIdUtil.generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header("norskident", ident)
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .toBodilessEntity()
                .map(resultat -> SigrunstubResponse.builder()
                        .ident(ident)
                        .status(HttpStatus.valueOf(resultat.getStatusCode().value()))
                        .build())
                .doOnError(
                        throwable -> !(throwable instanceof WebClientResponseException.NotFound),
                        WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(throwable -> SigrunstubResponse.of(WebClientError.describe(throwable), ident));
    }
}