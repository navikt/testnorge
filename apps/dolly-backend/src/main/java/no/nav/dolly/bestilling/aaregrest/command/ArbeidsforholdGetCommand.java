package no.nav.dolly.bestilling.aaregrest.command;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.aaregrest.domain.ArbeidsforholdRespons;
import no.nav.dolly.util.CallIdUtil;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.dto.aareg.v1.Arbeidsforhold;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_PERSON_IDENT;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@RequiredArgsConstructor
public class ArbeidsforholdGetCommand implements Callable<Mono<ArbeidsforholdRespons>> {

    private static final String AAREGDATA_URL = "/{miljoe}/api/v1/arbeidstaker/arbeidsforhold";

    private final WebClient webClient;
    private final String miljoe;
    private final String ident;
    private final String token;

    @Override
    public Mono<ArbeidsforholdRespons> call() {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(AAREGDATA_URL)
                        .build(miljoe))
                .header(HEADER_NAV_CALL_ID, CallIdUtil.generateCallId())
                .header(HEADER_NAV_PERSON_IDENT, ident)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .retrieve()
                .bodyToMono(Arbeidsforhold[].class)
                .map(arbeidsforhold1 -> ArbeidsforholdRespons.builder()
                        .eksisterendeArbeidsforhold(Arrays.asList(arbeidsforhold1))
                        .miljo(miljoe)
                        .build())
                .onErrorResume(error -> Mono.just(ArbeidsforholdRespons.builder()
                        .miljo(miljoe)
                        .error(error)
                        .build()))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
