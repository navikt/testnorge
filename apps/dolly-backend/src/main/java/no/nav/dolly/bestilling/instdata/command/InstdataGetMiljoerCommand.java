package no.nav.dolly.bestilling.instdata.command;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.instdata.domain.MiljoerResponse;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;

import static java.util.Collections.emptyList;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@RequiredArgsConstructor
public class InstdataGetMiljoerCommand implements Callable<Mono<List<String>>> {

    private static final String INSTMILJO_URL = "/api/v1/environment";

    private final WebClient webClient;
    private final String token;

    @Override
    public Mono<List<String>> call() {

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(INSTMILJO_URL)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .retrieve()
                .bodyToMono(MiljoerResponse.class)
                .map(MiljoerResponse::getInstitusjonsoppholdEnvironments)
                .doOnError(WebClientFilter::logErrorMessage)
                .onErrorResume(error -> Mono.just(emptyList()))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
