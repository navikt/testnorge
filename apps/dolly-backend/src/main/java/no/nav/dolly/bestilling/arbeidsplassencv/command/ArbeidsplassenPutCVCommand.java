package no.nav.dolly.bestilling.arbeidsplassencv.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.arbeidsplassencv.dto.ArbeidsplassenCVStatusDTO;
import no.nav.dolly.bestilling.arbeidsplassencv.dto.PAMCVDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.concurrent.Callable;

import static no.nav.dolly.bestilling.arbeidsplassencv.ArbeidsplassenCVConsumer.ARBEIDSPLASSEN_CALL_ID;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@RequiredArgsConstructor
@Slf4j
public class ArbeidsplassenPutCVCommand implements Callable<Flux<ArbeidsplassenCVStatusDTO>> {

    private static final String ARBEIDSPLASSEN_CV_URL = "/rest/v3/cv";
    private static final String FNR = "fnr";

    private final WebClient webClient;
    private final String ident;
    private final PAMCVDTO arbeidsplassenCV;
    private final String uuid;
    private final String token;

    @Override
    public Flux<ArbeidsplassenCVStatusDTO> call() {
        return webClient
                .put()
                .uri(
                        uriBuilder -> uriBuilder
                                .path(ARBEIDSPLASSEN_CV_URL)
                                .build())
                .header(FNR, ident)
                .header(ARBEIDSPLASSEN_CALL_ID, uuid)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .bodyValue(arbeidsplassenCV)
                .retrieve()
                .bodyToFlux(PAMCVDTO.class)
                .map(response -> ArbeidsplassenCVStatusDTO.builder()
                        .status(HttpStatus.OK)
                        .arbeidsplassenCV(response)
                        .uuid(uuid)
                        .build())
                .doOnError(throwable -> WebClientError.log(throwable, log))
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(throwable -> Flux.just(ArbeidsplassenCVStatusDTO.builder()
                        .status(WebClientError.describe(throwable).getStatus())
                        .feilmelding(WebClientError.describe(throwable).getMessage())
                        .uuid(uuid)
                        .build()));
    }

}
