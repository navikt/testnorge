package no.nav.dolly.bestilling.skattekort.command;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.skattekort.domain.SkattekortResponse;
import no.nav.dolly.util.RequestHeaderUtil;
import no.nav.testnav.libs.dto.skattekortservice.v1.SkattekortRequestDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
@Slf4j
public class SkattekortPostCommand implements Callable<Mono<SkattekortResponse>> {

    private static final String OPPRETT_SKATTEKORT_URL = "/skattekort/api/v1/person/opprett";
    private static final String CONSUMER = "Dolly";

    private final WebClient webClient;
    private final SkattekortRequestDTO request;
    private final String token;

    @Override
    public Mono<SkattekortResponse> call() {

        log.info("Sender skattekort til Sokos med request: {}", request);
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder.path(OPPRETT_SKATTEKORT_URL).build())
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .header(HEADER_NAV_CALL_ID, RequestHeaderUtil.getNavCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .headers(WebClientHeader.bearer(token))
                .bodyValue(request)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(body -> SkattekortResponse.builder()
                        .status(HttpStatus.OK)
                        .body(body)
                        .build())
                .doOnError(WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(throwable -> SkattekortResponse.of(WebClientError.describe(throwable)));
    }
}