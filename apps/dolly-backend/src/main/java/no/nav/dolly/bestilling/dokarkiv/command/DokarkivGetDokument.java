package no.nav.dolly.bestilling.dokarkiv.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.dokarkiv.domain.DokarkivResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.util.CallIdUtil.generateCallId;
import static org.apache.http.HttpHeaders.ACCEPT_ENCODING;

@Slf4j
@RequiredArgsConstructor
public class DokarkivGetDokument implements Callable<Mono<DokarkivResponse>> {

    private static final String DOKARDI_URL = "/{miljoe}/rest/hentdokument/{journalpostId}/{dokumentInfoId}/{variantFormat}";

    private final WebClient webClient;
    private final String miljoe;
    private final String journalpostId;
    private final String dokumentInfoId;
    private final String variantFormat;
    private final String token;

    @Override
    public Mono<DokarkivResponse> call() {

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(DOKARDI_URL)
                        .build(miljoe, journalpostId, dokumentInfoId, variantFormat))
                .header(HEADER_NAV_CALL_ID, generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header(ACCEPT_ENCODING, MediaType.APPLICATION_JSON_VALUE)
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> DokarkivResponse.builder()
                        .journalpostId(journalpostId)
                        .dokumenter(List.of(DokarkivResponse.DokumentInfo.builder()
                                .dokumentInfoId(dokumentInfoId)
                                .build()))
                        .dokument(response)
                        .build())
                .doOnError(throwable -> !(throwable instanceof WebClientResponseException.NotFound),
                        WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(throwable -> DokarkivResponse.of(WebClientError.describe(throwable), miljoe));
    }
}
