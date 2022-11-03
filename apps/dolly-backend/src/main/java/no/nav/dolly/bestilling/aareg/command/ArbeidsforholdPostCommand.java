package no.nav.dolly.bestilling.aareg.command;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.aareg.domain.ArbeidsforholdRespons;
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

import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_ARBEIDSFORHOLD;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@RequiredArgsConstructor
public class ArbeidsforholdPostCommand implements Callable<Flux<ArbeidsforholdRespons>> {

    private static final String AAREGDATA_URL = "/{miljoe}/api/v1/arbeidsforhold";
    private final WebClient webClient;
    private final String miljoe;
    private final Arbeidsforhold arbeidsforhold;
    private final String token;

    @Override
    public Flux<ArbeidsforholdRespons> call() {

        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path(AAREGDATA_URL)
                        .build(miljoe))
                .header(HEADER_NAV_ARBEIDSFORHOLD, CallIdUtil.generateCallId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .body(BodyInserters.fromValue(arbeidsforhold))
                .retrieve()
                .bodyToFlux(Arbeidsforhold.class)
                .map(arbeidsforhold1 -> ArbeidsforholdRespons.builder()
                        .arbeidsforhold(arbeidsforhold1)
                        .arbeidsforholdId(arbeidsforhold.getArbeidsforholdId())
                        .miljo(miljoe)
                        .build())
                .doOnError(WebClientFilter::logErrorMessage)
                .onErrorResume(error -> Flux.just(ArbeidsforholdRespons.builder()
                        .arbeidsforhold(arbeidsforhold)
                        .arbeidsforholdId(arbeidsforhold.getArbeidsforholdId())
                        .miljo(miljoe)
                        .error(error)
                        .build()))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
