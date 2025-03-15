package no.nav.dolly.bestilling.aareg.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.aareg.domain.ArbeidsforholdRespons;
import no.nav.dolly.util.CallIdUtil;
import no.nav.testnav.libs.dto.aareg.v1.Arbeidsforhold;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_ARBEIDSFORHOLD;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@RequiredArgsConstructor
@Slf4j
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
                .doOnError(throwable -> WebClientError.log(throwable, log))
                .onErrorResume(error -> Flux.just(ArbeidsforholdRespons.builder()
                        .arbeidsforholdId(arbeidsforhold.getArbeidsforholdId())
                        .miljo(miljoe)
                        .error(error)
                        .build()))
                .retryWhen(WebClientError.is5xxException());
    }

}
