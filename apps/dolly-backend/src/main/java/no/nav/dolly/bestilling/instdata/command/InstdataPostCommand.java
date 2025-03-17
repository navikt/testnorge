package no.nav.dolly.bestilling.instdata.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.instdata.domain.InstdataResponse;
import no.nav.dolly.domain.resultset.inst.Instdata;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.Callable;

import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@RequiredArgsConstructor
@Slf4j
public class InstdataPostCommand implements Callable<Mono<InstdataResponse>> {

    private static final String INSTDATA_URL = "/api/v1/institusjonsopphold/person";
    private static final String ENVIRONMENTS = "environments";

    private final WebClient webClient;
    private final Instdata instdata;
    private final String miljoe;
    private final String token;

    @Override
    public Mono<InstdataResponse> call() {
        return webClient
                .post()
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
                        .status(HttpStatus.valueOf(resultat.getStatusCode().value()))
                        .environments(List.of(miljoe))
                        .build())
                .doOnError(throwable -> WebClientError.log(throwable, log))
                .onErrorResume(throwable -> InstdataResponse.of(WebClientError.describe(throwable), instdata, List.of(miljoe)))
                .retryWhen(WebClientError.is5xxException());
    }

}
