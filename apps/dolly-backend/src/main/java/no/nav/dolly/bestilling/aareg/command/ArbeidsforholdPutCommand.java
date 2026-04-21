package no.nav.dolly.bestilling.aareg.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.aareg.domain.ArbeidsforholdRespons;
import no.nav.dolly.util.CallIdUtil;
import no.nav.testnav.libs.dto.aareg.v1.Arbeidsforhold;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.util.concurrent.Callable;

import static java.time.Duration.ofSeconds;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_ARBEIDSFORHOLD;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;

@RequiredArgsConstructor
@Slf4j
public class ArbeidsforholdPutCommand implements Callable<Flux<ArbeidsforholdRespons>> {

    private static final String AAREGDATA_URL = "/aareg/{miljoe}/api/v1/arbeidsforhold/{navArbeidsforholdId}";
    private static final String NAV_ARBEIDSFORHOLD_PERIODE = "Nav-Arbeidsforhold-Periode";

    private final WebClient webClient;
    private final String miljoe;
    private final Arbeidsforhold arbeidsforhold;
    private final String token;

    @Override
    public Flux<ArbeidsforholdRespons> call() {
        return webClient.put()
                .uri(uriBuilder -> uriBuilder.path(AAREGDATA_URL)
                        .build(miljoe, arbeidsforhold.getNavArbeidsforholdId()))
                .header(NAV_ARBEIDSFORHOLD_PERIODE, arbeidsforhold.getNavArbeidsforholdPeriode().toString())
                .header(HEADER_NAV_ARBEIDSFORHOLD, CallIdUtil.generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .headers(WebClientHeader.bearer(token))
                .body(BodyInserters.fromValue(arbeidsforhold))
                .retrieve()
                .bodyToFlux(Arbeidsforhold.class)
                .map(arbeidsforhold1 -> ArbeidsforholdRespons.builder()
                        .arbeidsforhold(arbeidsforhold1)
                        .miljoe(miljoe)
                        .build())
                .doOnError(WebClientError.logTo(log))
                .retryWhen(Retry.fixedDelay(3, ofSeconds(5))
                        .filter(throwable -> throwable instanceof WebClientResponseException responseException &&
                                responseException.getStatusCode().is5xxServerError())
                        .onRetryExhaustedThrow(((retryBackoffSpec, lastSignal) ->
                                new RuntimeException("Retries exhausted: %s".formatted(lastSignal.failure().getMessage())))))
                .onErrorResume(error -> Flux.just(ArbeidsforholdRespons.builder()
                        .miljoe(miljoe)
                        .error(error)
                        .build()));
    }
}