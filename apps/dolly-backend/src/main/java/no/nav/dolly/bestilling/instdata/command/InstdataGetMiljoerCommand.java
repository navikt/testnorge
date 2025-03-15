package no.nav.dolly.bestilling.instdata.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.instdata.domain.MiljoerResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.Callable;

import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@RequiredArgsConstructor
@Slf4j
public class InstdataGetMiljoerCommand implements Callable<Mono<List<String>>> {

    private static final String INSTMILJO_URL = "/api/v1/environment";

    private final WebClient webClient;
    private final String token;

    @Override
    public Mono<List<String>> call() {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(INSTMILJO_URL)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .retrieve()
                .bodyToMono(MiljoerResponse.class)
                .map(MiljoerResponse::getInstitusjonsoppholdEnvironments)
                .doOnError(throwable -> WebClientError.log(throwable, log))
                .onErrorResume(error -> Mono.just(List.of("q1", "q2")))
                .retryWhen(WebClientError.is5xxException());
    }

}
