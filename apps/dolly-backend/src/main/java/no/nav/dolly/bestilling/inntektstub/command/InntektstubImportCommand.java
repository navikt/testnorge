package no.nav.dolly.bestilling.inntektstub.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.inntektstub.domain.ImportRequest;
import no.nav.dolly.bestilling.inntektstub.domain.ImportResponse;
import no.nav.dolly.bestilling.inntektstub.domain.Inntektsinformasjon;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class InntektstubImportCommand implements Callable<Mono<ImportResponse>> {

    private static final String INNTEKTER_URL = "/inntektstub/api/v2/import";

    private final WebClient webClient;
    private final String ident;
    private final String token;

    @Override
    public Mono<ImportResponse> call() {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(INNTEKTER_URL)
                        .build())
                .headers(WebClientHeader.bearer(token))
                .bodyValue(new ImportRequest(ident))
                .retrieve()
                .toBodilessEntity()
                .map(response -> ImportResponse.builder()
                        .status(HttpStatus.valueOf(response.getStatusCode().value()))
                        .build())
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(throwable -> {
                    var description = WebClientError.describe(throwable);
                    log.error("Import av Instdata for {} feilet: {}",
                            ident, description.getMessage(), throwable);
                    return Mono.just(ImportResponse.builder()
                            .status(description.getStatus())
                            .message(description.getMessage())
                            .build());
                });
    }
}