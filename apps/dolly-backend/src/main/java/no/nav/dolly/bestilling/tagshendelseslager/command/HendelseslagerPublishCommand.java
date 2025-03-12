package no.nav.dolly.bestilling.tagshendelseslager.command;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.tagshendelseslager.dto.HendelselagerResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.Callable;

import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@RequiredArgsConstructor
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
                        .status(WebClientFilter.getStatus(throwable))
                        .feilmelding(WebClientFilter.getMessage(throwable))
                        .build()))
                .doOnError(WebClientFilter::logErrorMessage)
                .retryWhen(WebClientError.is5xxException());
    }

}
