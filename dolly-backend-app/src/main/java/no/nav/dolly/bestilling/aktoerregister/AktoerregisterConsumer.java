package no.nav.dolly.bestilling.aktoerregister;

import static java.lang.String.format;
import static no.nav.dolly.domain.CommonKeys.CONSUMER;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_PERSON_IDENTER;
import static org.apache.cxf.helpers.HttpHeaderHelper.AUTHORIZATION;

import java.net.URI;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.properties.ProvidersProps;
import no.nav.dolly.security.sts.StsOidcService;

@Service
@RequiredArgsConstructor
public class AktoerregisterConsumer {

    private static final String AKTOER_URL = "/api/v1/identer";
    private static final String PREPROD_ENV = "q";

    private final ProvidersProps providersProps;
    private final RestTemplate restTemplate;
    private final StsOidcService stsOidcService;

    @Timed(name = "providers", tags = { "operation", "aktoerregister_getId" })
    public ResponseEntity<Map> getAktoerId(String ident) {
        return restTemplate.exchange(RequestEntity.get(
                URI.create(providersProps.getAktoerregister().getUrl() + AKTOER_URL))
                .header(AUTHORIZATION, stsOidcService.getIdToken(PREPROD_ENV))
                .header(HEADER_NAV_CALL_ID, getNavCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header(HEADER_NAV_PERSON_IDENTER, ident)
                .build(), Map.class);
    }

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID().toString());
    }
}
