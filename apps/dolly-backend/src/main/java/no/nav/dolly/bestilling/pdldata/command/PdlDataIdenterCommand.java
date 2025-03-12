package no.nav.dolly.bestilling.pdldata.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@RequiredArgsConstructor
public class PdlDataIdenterCommand implements Callable<Mono<Void>> {

    private static final String PDL_FORVALTER_IDENTER_URL = "/api/v1/identer/{ident}";

    private final WebClient webClient;
    private final String url;
    private final String ident;
    private final String token;

    public Mono<Void> call() {
        return webClient
                .put()
                .uri(uriBuilder -> uriBuilder.path(PDL_FORVALTER_IDENTER_URL)
                        .path(url)
                        .build(ident))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .retrieve()
                .bodyToMono(Void.class)
                .doOnError(WebClientFilter::logErrorMessage)
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(throwable -> throwable instanceof WebClientResponseException.NotFound,
                        throwable -> Mono.empty());
    }

}
