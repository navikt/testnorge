package no.nav.dolly.bestilling.sigrunstub;

import static java.lang.String.format;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.security.sts.StsOidcService.getUserIdToken;
import static no.nav.dolly.security.sts.StsOidcService.getUserPrinciple;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.resultset.sigrunstub.OpprettSkattegrunnlag;
import no.nav.dolly.properties.ProvidersProps;

@Slf4j
@Component
@RequiredArgsConstructor
public class SigrunStubConsumer {

    private static final String CONSUMER = "Dolly";
    private static final String URL_VERSION = "/api/v1";
    private static final String SIGRUN_STUB_DELETE_GRUNNLAG = URL_VERSION + "/slett";
    private static final String SIGRUN_STUB_OPPRETT_GRUNNLAG = URL_VERSION + "/lignetinntekt";

    private final ProvidersProps providersProps;
    private final RestTemplate restTemplate;

    public void deleteSkattegrunnlag(String ident) {

        restTemplate.exchange(RequestEntity.delete(URI.create(providersProps.getSigrunStub().getUrl() + SIGRUN_STUB_DELETE_GRUNNLAG))
                        .header(AUTHORIZATION, getUserIdToken())
                        .header(HEADER_NAV_CALL_ID, getNavCallId())
                        .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                        .header("personidentifikator", ident)
                        .build(),
                String.class);
    }

    public ResponseEntity createSkattegrunnlag(List<OpprettSkattegrunnlag> request) {

        return restTemplate.exchange(RequestEntity.post(URI.create(providersProps.getSigrunStub().getUrl() + SIGRUN_STUB_OPPRETT_GRUNNLAG))
                        .header(ACCEPT, APPLICATION_JSON_VALUE)
                        .header(AUTHORIZATION, getUserIdToken())
                        .header(HEADER_NAV_CALL_ID, getNavCallId())
                        .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                        .header("testdataEier", getUserPrinciple())
                        .body(request),
                Object.class);
    }

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID().toString());
    }
}