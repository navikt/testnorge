package no.nav.dolly.bestilling.arbeidsplassencv.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.arbeidsplassencv.dto.ArbeidsplassenCVStatusDTO;
import no.nav.dolly.bestilling.arbeidsplassencv.dto.PAMCVDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import static no.nav.dolly.bestilling.arbeidsplassencv.ArbeidsplassenCVConsumer.ARBEIDSPLASSEN_CALL_ID;

@RequiredArgsConstructor
@Slf4j
public class ArbeidsplassenPutCVCommand implements Callable<Mono<ArbeidsplassenCVStatusDTO>> {

    private static final String ARBEIDSPLASSEN_CV_URL = "/rest/v3/cv";
    private static final String FNR = "fnr";

    private final WebClient webClient;
    private final String ident;
    private final PAMCVDTO arbeidsplassenCV;
    private final String uuid;
    private final String token;

    @Override
    public Mono<ArbeidsplassenCVStatusDTO> call() {
        return webClient
                .put()
                .uri(
                        uriBuilder -> uriBuilder
                                .path(ARBEIDSPLASSEN_CV_URL)
                                .build())
                .header(FNR, ident)
                .header(ARBEIDSPLASSEN_CALL_ID, uuid)
                .headers(WebClientHeader.bearer(token))
                .bodyValue(arbeidsplassenCV)
                .retrieve()
                .bodyToMono(PAMCVDTO.class)
                .map(response -> ArbeidsplassenCVStatusDTO.builder()
                        .status(HttpStatus.OK)
                        .arbeidsplassenCV(response)
                        .uuid(uuid)
                        .build())
                .doOnError(WebClientError.logTo(log))

                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(throwable -> ArbeidsplassenCVStatusDTO.of(WebClientError.describe(throwable), uuid));
    }

}
