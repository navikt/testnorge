package no.nav.dolly.bestilling.krrstub.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.util.RequestHeaderUtil;
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

@Slf4j
@RequiredArgsConstructor
public class DeleteKontaktadataCommand implements Callable<Flux<Void>> {

    private static final String DIGITAL_KONTAKT_URL = "/api/v2/kontaktinformasjon";

    private final WebClient webClient;
    private final Long id;
    private final String token;

    @Timed(name = "providers", tags = { "operation", "krrstub_deleteKontaktdata" })
    public Flux<Void> call() {

        return webClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path(DIGITAL_KONTAKT_URL)
                        .pathSegment(id.toString())
                        .build())
                .header(HEADER_NAV_CALL_ID, RequestHeaderUtil.getNavCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .retrieve()
                .bodyToFlux(Void.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .doOnError(throwable -> log.error(WebClientFilter.getMessage(throwable)));
    }
}
