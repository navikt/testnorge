package no.nav.dolly.bestilling.inntektstub.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.concurrent.Callable;

import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@RequiredArgsConstructor
public class InntektstubDeleteCommand implements Callable<Flux<String>> {

    private static final String DELETE_INNTEKTER_URL = "/api/v2/personer";
    private static final String NORSKE_IDENTER_QUERY = "norske-identer";

    private final WebClient webClient;
    private final List<String> identer;
    private final String token;

    public Flux<String> call() {
        return webClient
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path(DELETE_INNTEKTER_URL)
                        .queryParam(NORSKE_IDENTER_QUERY, identer)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .retrieve()
                .bodyToFlux(Void.class)
                .map(respons -> "")
                .doOnError(WebClientFilter::logErrorMessage)
                .retryWhen(WebClientError.is5xxException());
    }

}
