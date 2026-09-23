package no.nav.dolly.bestilling.inntektstub.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.inntektstub.domain.CheckImportRequest;
import no.nav.dolly.bestilling.inntektstub.domain.CheckImportResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Slf4j
@RequiredArgsConstructor
public class InntektstubCheckImportCommand implements Callable<Mono<CheckImportResponse>> {

    private static final String INNTEKTER_URL = "/inntektstub/api/v2/import";
    private static final String CHECK = "check";

    private final WebClient webClient;
    private final Boolean isCheck;
    private final String ident;
    private final String token;

    @Override
    public Mono<CheckImportResponse> call() {
        return webClient
                .post()
                .uri(uriBuilder -> {
                    uriBuilder.path(INNTEKTER_URL);
                    if (isTrue(isCheck)) {
                        uriBuilder.pathSegment(CHECK);
                    }
                    return uriBuilder.build();
                })
                .headers(WebClientHeader.bearer(token))
                .bodyValue(new CheckImportRequest(ident))
                .retrieve()
                .toBodilessEntity()
                .map(response -> CheckImportResponse.builder()
                        .status(HttpStatus.valueOf(response.getStatusCode().value()))
                        .build())
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(error -> {
                    var description = WebClientError.describe(error);
                    if (description.getStatus().is5xxServerError()) {
                        log.error("Import av inntekt fra Tenor for {} feilet: {}", ident, description.getMessage(), error);
                    }
                    return Mono.just(CheckImportResponse.builder()
                            .status(description.getStatus())
                            .message(description.getMessage())
                            .build());
                });
    }
}