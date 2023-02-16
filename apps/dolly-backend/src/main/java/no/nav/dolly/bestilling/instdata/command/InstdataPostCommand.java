package no.nav.dolly.bestilling.instdata.command;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.instdata.domain.InstdataResponse;
import no.nav.dolly.domain.resultset.inst.Instdata;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;

import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@RequiredArgsConstructor
public class InstdataPostCommand implements Callable<Mono<InstdataResponse>> {

    private static final String INSTDATA_URL = "/api/v1/institusjonsopphold/person";
    private static final String ENVIRONMENTS = "environments";

    private final WebClient webClient;
    private final Instdata instdata;
    private final String miljoe;
    private final String token;

    @Override
    public Mono<InstdataResponse> call() {

        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(INSTDATA_URL)
                        .queryParam(ENVIRONMENTS, miljoe)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .bodyValue(instdata)
                .retrieve()
                .toBodilessEntity()
                .map(resultat -> InstdataResponse.builder()
                        .personident(instdata.getNorskident())
                        .instdata(instdata)
                        .status(resultat.getStatusCode())
                        .environments(List.of(miljoe))
                        .build())
                .doOnError(WebClientFilter::logErrorMessage)
                .onErrorResume(error -> Mono.just(InstdataResponse.builder()
                        .personident(instdata.getNorskident())
                        .instdata(instdata)
                        .status(WebClientFilter.getStatus(error))
                        .feilmelding(WebClientFilter.getMessage(error))
                        .environments(List.of(miljoe))
                        .build()))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
