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
import reactor.core.publisher.Flux;

import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.*;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@RequiredArgsConstructor
@Slf4j
public class ArbeidsforholdPutCommand implements Callable<Flux<ArbeidsforholdRespons>> {

    private static final String AAREGDATA_URL = "/{miljoe}/api/v1/arbeidsforhold/{navArbeidsforholdId}";
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
                .headers(WebClientHeader.jwt(getUserJwt()))
                .body(BodyInserters.fromValue(arbeidsforhold))
                .retrieve()
                .bodyToFlux(Arbeidsforhold.class)
                .map(arbeidsforhold1 -> ArbeidsforholdRespons.builder()
                        .arbeidsforhold(arbeidsforhold1)
                        .arbeidsforholdId(arbeidsforhold.getArbeidsforholdId())
                        .miljo(miljoe)
                        .build())
                .doOnError(WebClientError.logTo(log))
                .onErrorResume(error -> Flux.just(ArbeidsforholdRespons.builder()
                        .arbeidsforholdId(arbeidsforhold.getArbeidsforholdId())
                        .miljo(miljoe)
                        .error(error)
                        .build()))
                .retryWhen(WebClientError.is5xxException());
    }

}
