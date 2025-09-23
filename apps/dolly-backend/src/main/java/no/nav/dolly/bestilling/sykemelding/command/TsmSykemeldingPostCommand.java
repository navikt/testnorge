package no.nav.dolly.bestilling.sykemelding.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.sykemelding.domain.TsmSykemeldingRequest;
import no.nav.dolly.bestilling.sykemelding.dto.NySykemeldingResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class TsmSykemeldingPostCommand implements Callable<Mono<NySykemeldingResponse>> {

    private static final String TSM_SYKEMELDING_URL = "/tsm/api/sykmelding";

    private final WebClient webClient;
    private final TsmSykemeldingRequest request;
    private final String token;

    @Override
    public Mono<NySykemeldingResponse> call() {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder.path(TSM_SYKEMELDING_URL).build())
                .headers(WebClientHeader.bearer(token))
                .bodyValue(request)
                .retrieve()
                .bodyToMono(NySykemeldingResponse.class)
                .doOnError(WebClientError.logTo(log))
                .onErrorResume(error -> Mono.just(new NySykemeldingResponse(error.getMessage(), "NA", null, request.getIdent())));

    }
}