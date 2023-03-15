package no.nav.dolly.bestilling.arbeidsplassencv.command;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.arbeidsplassencv.dto.CVDeleteDTO;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.dto.arbeidsplassencv.v1.ArbeidsplassenCVDTO;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@RequiredArgsConstructor
public class ArbeidsplassenDeleteCVCommand implements Callable<Flux<ArbeidsplassenCVDTO>> {

    private static final String ARBEIDSPLASSEN_CV_URL = "/rest/v2/cv";
    private static final String FNR = "fnr";

    private final WebClient webClient;
    private final String ident;
    private final CVDeleteDTO cvDelete;
    private final String token;

    @Override
    public Flux<ArbeidsplassenCVDTO> call() {

        return webClient.delete().uri(
                        uriBuilder -> uriBuilder
                                .path(ARBEIDSPLASSEN_CV_URL)
                                .build())
                .header(FNR, ident)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .retrieve()
                .bodyToFlux(ArbeidsplassenCVDTO.class)
                .doOnError(WebClientFilter::logErrorMessage)
                .onErrorResume(throwable -> Flux.empty())
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
