package no.nav.dolly.bestilling.aaregrest.command;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.aaregrest.domain.ArbeidsforholdRespons;
import no.nav.dolly.util.CallIdUtil;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.dto.aareg.v1.Arbeidsforhold;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_ARBEIDSFORHOLD;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@RequiredArgsConstructor
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
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .body(BodyInserters.fromValue(arbeidsforhold))
                .retrieve()
                .bodyToFlux(Arbeidsforhold.class)
                .map(arbeidsforhold1 -> ArbeidsforholdRespons.builder()
                        .arbeidsforhold(arbeidsforhold1)
                        .miljo(miljoe)
                        .build())
                .onErrorResume(error -> Flux.just(ArbeidsforholdRespons.builder()
                        .miljo(miljoe)
                        .error(error)
                        .build()))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
