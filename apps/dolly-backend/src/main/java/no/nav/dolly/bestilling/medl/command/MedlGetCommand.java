package no.nav.dolly.bestilling.medl.command;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.domain.resultset.medl.MedlDataResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

import java.util.concurrent.Callable;

import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@RequiredArgsConstructor
public class MedlGetCommand implements Callable<Flux<MedlDataResponse>> {

    private static final String MEDL_URL = "/rest/v1/person/{ident}";

    private final WebClient webClient;
    private final String ident;
    private final String token;

    public Flux<MedlDataResponse> call() {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(MEDL_URL)
                        .build(ident))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .retrieve()
                .bodyToFlux(MedlDataResponse.class)
                .doOnError(throwable -> !(throwable instanceof WebClientResponseException.NotFound), WebClientFilter::logErrorMessage)
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(WebClientResponseException.NotFound.class::isInstance, throwable -> Flux.empty());
    }

}
