package no.nav.dolly.bestilling.aareg.command;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.aareg.domain.AaregResponse;
import no.nav.dolly.util.CallIdUtil;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@RequiredArgsConstructor
public class AaregDeleteCommand implements Callable<Flux<AaregResponse>> {

    private static final String AAREGDATA_URL = "/api/v1/arbeidsforhold";
    private static final String IDENT_QUERY = "ident";

    private final WebClient webClient;
    private final String ident;
    private final String token;

    public Flux<AaregResponse> call() {

        return webClient.delete()
                .uri(uriBuilder -> uriBuilder.path(AAREGDATA_URL)
                        .queryParam(IDENT_QUERY, ident)
                        .build())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header(HEADER_NAV_CALL_ID, CallIdUtil.generateCallId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .retrieve()
                .bodyToFlux(AaregResponse.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
