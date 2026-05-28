package no.nav.dolly.bestilling.instdata.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.instdata.domain.MiljoerResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class InstdataGetMiljoerCommand implements Callable<Mono<MiljoerResponse>> {

    private static final String INSTMILJO_URL = "/inst/api/v1/environment";

    private final WebClient webClient;
    private final String token;

    @Override
    public Mono<MiljoerResponse> call() {

        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(INSTMILJO_URL)
                        .build())
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToMono(MiljoerResponse.class)
                .doOnError(WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(error ->
                        Mono.just(MiljoerResponse.builder()
                                .institusjonsoppholdEnvironments(List.of("q1", "q2"))
                                .kdiEnvironments(List.of("q2"))
                                .build()))
                .cache(Duration.ofSeconds(60));
    }
}