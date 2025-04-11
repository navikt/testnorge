package no.nav.dolly.bestilling.dokarkiv.command;

import co.elastic.clients.util.ContentType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.dokarkiv.domain.DokarkivResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.util.CallIdUtil.generateCallId;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;
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
                .header(ACCEPT_ENCODING, ContentType.APPLICATION_JSON)
                .headers(WebClientHeader.bearer(token))
                .headers(WebClientHeader.jwt(getUserJwt()))
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> DokarkivResponse.builder()
                        .journalpostId(journalpostId)
                        .dokumenter(List.of(DokarkivResponse.DokumentInfo.builder()
                                .dokumentInfoId(dokumentInfoId)
                                .build()))
                        .dokument(response)
                        .build())
                .retryWhen(WebClientError.is5xxException())
                .doOnError(WebClientError.logTo(log))
                .onErrorResume(throwable -> DokarkivResponse.of(WebClientError.describe(throwable), miljoe));
    }
}
