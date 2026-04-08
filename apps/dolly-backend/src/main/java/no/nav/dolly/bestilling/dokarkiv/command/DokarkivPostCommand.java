package no.nav.dolly.bestilling.dokarkiv.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.dokarkiv.domain.DokarkivRequest;
import no.nav.dolly.bestilling.dokarkiv.domain.DokarkivResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.Callable;

import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@RequiredArgsConstructor
@Slf4j
public class DokarkivPostCommand implements Callable<Mono<DokarkivResponse>> {

    private static final Duration RESPONSE_TIMEOUT = Duration.ofMinutes(5);

    private final WebClient webClient;
    private final String environment;
    private final DokarkivRequest dokarkivRequest;
    private final String token;

    @Override
    public Mono<DokarkivResponse> call() {

        logRequestSize();

        return webClient.mutate()
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .responseTimeout(RESPONSE_TIMEOUT)))
                .build()
                .post()
                .uri(builder ->
                        builder.path("/dokarkiv/api/{miljo}/v1/journalpost")
                                .queryParam("forsoekFerdigstill", isTrue(dokarkivRequest.getFerdigstill()))
                                .build(environment))
                .headers(WebClientHeader.bearer(token))
                .bodyValue(dokarkivRequest)
                .retrieve()
                .bodyToMono(DokarkivResponse.class)
                .map(response -> {
                    response.setMiljoe(environment);
                    return response;
                })
                .doOnError(WebClientResponseException.class, this::logDetailedError)
                .doOnError(WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(throwable -> DokarkivResponse.of(WebClientError.describe(throwable), environment));
    }

    private void logRequestSize() {

        var totalChars = dokarkivRequest.getDokumenter().stream()
                .flatMap(dok -> dok.getDokumentvarianter().stream())
                .mapToInt(variant -> isNotBlank(variant.getFysiskDokument())
                        ? variant.getFysiskDokument().length() : 0)
                .sum();

        log.info("Dokarkiv POST til miljoe {}: {} dokumentvarianter, totalt {} tegn fysiskDokument (~{} MB estimert JSON)",
                environment,
                dokarkivRequest.getDokumenter().stream()
                        .mapToInt(dok -> dok.getDokumentvarianter().size()).sum(),
                totalChars,
                String.format("%.1f", totalChars / (1024.0 * 1024.0)));
    }

    private void logDetailedError(WebClientResponseException error) {

        var headers = error.getHeaders();
        log.warn("Dokarkiv feilet med status {} for miljoe {}. Respons-headere: Content-Type={}, Content-Length={}, Via={}, Server={}. Body: {}",
                error.getStatusCode().value(),
                environment,
                headers.getFirst("Content-Type"),
                headers.getFirst("Content-Length"),
                headers.getFirst("Via"),
                headers.getFirst("Server"),
                error.getResponseBodyAsString());
    }

}
