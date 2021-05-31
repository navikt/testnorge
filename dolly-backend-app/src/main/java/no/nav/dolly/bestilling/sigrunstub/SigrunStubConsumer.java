package no.nav.dolly.bestilling.sigrunstub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.resultset.sigrunstub.OpprettSkattegrunnlag;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.properties.ProvidersProps;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static java.lang.String.format;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Component
@RequiredArgsConstructor
public class SigrunStubConsumer {

    private static final String CONSUMER = "Dolly";
    private static final String URL_VERSION = "/api/v1";
    private static final String SIGRUN_STUB_DELETE_GRUNNLAG = URL_VERSION + "/slett";
    private static final String SIGRUN_STUB_OPPRETT_GRUNNLAG = URL_VERSION + "/lignetinntekt";

    private final RestTemplate restTemplate;
    private final ProvidersProps providersProps;

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID().toString());
    }

    @Timed(name = "providers", tags = {"operation", "sigrun_deleteGrunnlag"})
    public void deleteSkattegrunnlag(String ident) {

        var callId = getNavCallId();
        log.info("Delete til sigrun-stub med call-id {}", callId);

        restTemplate.exchange(RequestEntity.delete(URI.create(providersProps.getSigrunStub().getUrl() + SIGRUN_STUB_DELETE_GRUNNLAG))
                        .header(HEADER_NAV_CALL_ID, callId)
                        .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                        .header("personidentifikator", ident)
                        .build(),
                String.class);
    }

    @Timed(name = "providers", tags = {"operation", "sigrun_createGrunnlag"})
    public ResponseEntity createSkattegrunnlag(List<OpprettSkattegrunnlag> request) {

        var callId = getNavCallId();
        log.info("Post til sigrun-stub med call-id {} og data {}", callId, request);

        return restTemplate.exchange(RequestEntity.post(URI.create(providersProps.getSigrunStub().getUrl() + SIGRUN_STUB_OPPRETT_GRUNNLAG))
                        .header(ACCEPT, APPLICATION_JSON_VALUE)
                        .header(HEADER_NAV_CALL_ID, callId)
                        .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                        .body(request),
                Object.class);
    }
}