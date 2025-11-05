package no.nav.dolly.bestilling.arbeidsplassencv.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.arbeidsplassencv.dto.ArbeidsplassenCVStatusDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static no.nav.dolly.bestilling.arbeidsplassencv.ArbeidsplassenCVConsumer.ARBEIDSPLASSEN_CALL_ID;

@RequiredArgsConstructor
@Slf4j
public class ArbeidsplassenGodtaVilkaarCommand implements Callable<Mono<ArbeidsplassenCVStatusDTO>> {

    private static final String ARBEIDSPLASSEN_SAMTYKKE_URL = "/rest/godta-vilkar";
    private static final String FNR = "fnr";

    private final WebClient webClient;
    private final String ident;
    private final String uuid;
    private final String token;

    @Override
    public Mono<ArbeidsplassenCVStatusDTO> call() {

        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(ARBEIDSPLASSEN_SAMTYKKE_URL)
                        .build())
                .header(FNR, ident)
                .header(ARBEIDSPLASSEN_CALL_ID, uuid)
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .toBodilessEntity()
                .map(response -> ArbeidsplassenCVStatusDTO.builder()
                        .status(HttpStatus.OK)
                        .uuid(uuid)
                        .build())
                .doOnError(WebClientError.logTo(log))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                        .filter(throwable -> !(throwable instanceof WebClientResponseException internal &&
                                internal.getResponseBodyAsString().contains("Greide ikke å godta gjeldende hjemmel"))))
                .onErrorResume(throwable -> ArbeidsplassenCVStatusDTO.of(WebClientError.describe(throwable), uuid));
    }
}