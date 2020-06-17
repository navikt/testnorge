package no.nav.dolly.bestilling.dokarkiv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.properties.ProvidersProps;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.UUID;

import static java.lang.String.format;
import static no.nav.dolly.domain.CommonKeys.CONSUMER;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CONSUMER_ID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DokarkivConsumer {

    private static final String DOKARKIV_URL = "/api/v1/dokarkiv";

    private final RestTemplate restTemplate;
    private final ProvidersProps providersProps;

    @Timed(name = "providers", tags = { "operation", "dokarkiv-opprett" })
    public ResponseEntity postDokarkiv(DokarkivRequest dokarkivRequest) {
        return restTemplate.exchange(
                RequestEntity.post(URI.create(providersProps.getDokarkiv().getUrl() + DOKARKIV_URL))
                .header(HEADER_NAV_CALL_ID, getNavCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .body(dokarkivRequest), DokarkivResponse.class);
        )
    }

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID().toString());
    }
}
