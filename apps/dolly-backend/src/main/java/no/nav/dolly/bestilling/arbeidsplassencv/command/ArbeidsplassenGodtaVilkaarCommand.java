package no.nav.dolly.bestilling.arbeidsplassencv.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.arbeidsplassencv.dto.ArbeidsplassenCVStatusDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import static no.nav.dolly.bestilling.arbeidsplassencv.ArbeidsplassenCVConsumer.ARBEIDSPLASSEN_CALL_ID;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@RequiredArgsConstructor
@Slf4j
public class ArbeidsplassenGodtaVilkaarCommand implements Callable<Mono<ArbeidsplassenCVStatusDTO>> {

    private static final String ARBEIDSPLASSEN_SAMTYKKE_URL = "/rest/godta-vilkar";
    private static final String FNR = "fnr";

    private final WebClient webClient;
    private final String ident;
    private final String uuid;
    private final String token;

    @Override
    public Mono<ArbeidsplassenCVStatusDTO> call() {
        return webClient
                .post()
                .uri(
                        uriBuilder -> uriBuilder
                                .path(ARBEIDSPLASSEN_SAMTYKKE_URL)
                                .build())
                .header(FNR, ident)
                .header(ARBEIDSPLASSEN_CALL_ID, uuid)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .retrieve()
                .toBodilessEntity()
                .map(response -> ArbeidsplassenCVStatusDTO.builder()
                        .status(HttpStatus.OK)
                        .uuid(uuid)
                        .build())
                .doOnError(throwable -> WebClientError.log(throwable, log))
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(throwable -> ArbeidsplassenCVStatusDTO.of(WebClientError.describe(throwable), uuid));
    }

}
