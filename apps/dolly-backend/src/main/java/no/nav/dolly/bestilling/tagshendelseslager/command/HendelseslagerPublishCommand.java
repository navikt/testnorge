package no.nav.dolly.bestilling.tagshendelseslager.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.tagshendelseslager.dto.HendelselagerResponse;
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
public class HendelseslagerPublishCommand implements Callable<Mono<HendelselagerResponse>> {

    private static final String PDL_PUBLISH = "/internal/publish";
    private static final String PDL_IDENTHENDELSE = "/pdl-identhendelse";
    private static final String PERSON_IDENTER = "Nav-Personidenter";

    private final WebClient webClient;
    private final List<String> identer;
    private final String token;

    public Mono<HendelselagerResponse> call() {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(PDL_IDENTHENDELSE)
                        .path(PDL_PUBLISH)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .header(PERSON_IDENTER, String.join(",", identer))
                .retrieve()
                .toEntity(String.class)
                .map(resultat -> HendelselagerResponse.builder()
                        .status(HttpStatus.valueOf(resultat.getStatusCode().value()))
                        .body(resultat.getBody())
                        .build())
                .onErrorResume(throwable -> Mono.just(HendelselagerResponse.builder()
                        .status(WebClientError.describe(throwable).getStatus())
                        .feilmelding(WebClientError.describe(throwable).getMessage())
                        .build()))
                .doOnError(throwable -> WebClientError.log(throwable, log))
                .retryWhen(WebClientError.is5xxException());
    }

}
