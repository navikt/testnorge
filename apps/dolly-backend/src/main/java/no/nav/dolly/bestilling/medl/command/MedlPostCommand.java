package no.nav.dolly.bestilling.medl.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.medl.dto.MedlPostResponse;
import no.nav.dolly.domain.resultset.medl.MedlData;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class MedlPostCommand implements Callable<Mono<MedlPostResponse>> {

    private static final String MEDL_URL = "/rest/v1/medlemskapsperiode";

    private final WebClient webClient;
    private final MedlData medlData;
    private final String token;

    @Override
    public Mono<MedlPostResponse> call() {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(MEDL_URL)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .headers(WebClientHeader.bearer(token))
                .bodyValue(medlData)
                .retrieve()
                .toBodilessEntity()
                .map(response -> {
                    log.info("Mottok response fra Medl post: \n{}", response);
                    return MedlPostResponse.builder()
                            .status(HttpStatus.valueOf(response.getStatusCode().value()))
                            .build();
                })
                .doOnError(WebClientError.logTo(log))
                .onErrorResume(throwable -> MedlPostResponse.of(WebClientError.describe(throwable)));
    }
}
