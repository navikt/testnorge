package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command.pensjon;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.pensjon.PensjonTestdataInntekt;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.pensjon.PensjonTestdataResponse;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.util.Headers.AUTHORIZATION;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.util.Headers.CALL_ID;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.util.Headers.CONSUMER_ID;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.util.Headers.NAV_CALL_ID;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.util.Headers.NAV_CONSUMER_ID;

@Slf4j
public class PostPensjonTestdataInntektCommand implements Callable<Mono<PensjonTestdataResponse>> {
    private final WebClient webClient;
    private final PensjonTestdataInntekt inntekt;
    private final String idToken;

    public PostPensjonTestdataInntektCommand(WebClient webclient, PensjonTestdataInntekt inntekt, String idToken) {
        this.webClient = webclient;
        this.inntekt = inntekt;
        this.idToken = idToken;
    }

    @Override
    public Mono<PensjonTestdataResponse> call() {
        log.info("Oppretter ny pensjon testdata inntekt: \n{}", inntekt);
        return webClient.post()
                .uri(builder ->
                        builder.path("/api/v1/inntekt").build()
                )
                .header(CALL_ID, NAV_CALL_ID)
                .header(CONSUMER_ID, NAV_CONSUMER_ID)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, "Bearer " + idToken)
                .body(BodyInserters.fromPublisher(Mono.just(inntekt), PensjonTestdataInntekt.class))
                .retrieve()
                .bodyToMono(PensjonTestdataResponse.class)
                .doOnError(WebClientFilter::logErrorMessage)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
