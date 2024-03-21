package no.nav.testnav.kodeverkservice.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.kodeverkservice.domain.KodeverkBetydningerResponse;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static no.nav.testnav.kodeverkservice.utility.CallIdUtil.generateCallId;
import static no.nav.testnav.kodeverkservice.utility.CommonKeysAndUtils.CONSUMER;
import static no.nav.testnav.kodeverkservice.utility.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.testnav.kodeverkservice.utility.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.testnav.kodeverkservice.utility.TokenXUtil.getUserJwt;

@RequiredArgsConstructor
public class KodeverkGetCommand implements Callable<Flux<KodeverkBetydningerResponse>> {

    private static final String KODEVERK_URL_BEGINNING = "/api/v1/kodeverk";
    private static final String KODEVERK_URL_KODER = "koder";
    private static final String KODEVERK_URL_BETYDNINGER = "betydninger";

    private final WebClient webClient;
    private final String kodeverk;
    private final String token;

    @Override
    public Flux<KodeverkBetydningerResponse> call() {

        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(KODEVERK_URL_BEGINNING)
                        .pathSegment(kodeverk)
                        .pathSegment(KODEVERK_URL_KODER)
                        .pathSegment(KODEVERK_URL_BETYDNINGER)
                        .queryParam("ekskluderUgyldige", true)
                        .queryParam("spraak", "nb")
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header(HEADER_NAV_CALL_ID, generateCallId())
                .retrieve()
                .bodyToFlux(KodeverkBetydningerResponse.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
