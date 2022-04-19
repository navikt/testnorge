package no.nav.dolly.bestilling.arenaforvalter.command;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.util.CallIdUtil.generateCallId;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@RequiredArgsConstructor
public class ArenaForvalterGetMiljoeCommand implements Callable<Flux<String>> {

    private static final String ARENAFORVALTER_ENVIRONMENTS = "/api/v1/miljoe";

    private final WebClient webClient;
    private final String token;

    @Override
    public Flux<String> call() {

        return webClient.get().uri(
                        uriBuilder -> uriBuilder
                                .path(ARENAFORVALTER_ENVIRONMENTS)
                                .build())
                .header(HEADER_NAV_CALL_ID, generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .retrieve()
                .bodyToMono(String[].class)
                .flatMapIterable(miljoer -> Arrays.stream(miljoer).toList())
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
