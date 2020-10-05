package no.nav.dolly.bestilling.sykemelding;

import static java.lang.String.format;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;

import java.net.URI;
import java.util.UUID;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.sykemelding.domain.DetaljertSykemeldingRequest;
import no.nav.dolly.bestilling.sykemelding.domain.SyntSykemeldingRequest;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.properties.ProvidersProps;

@Slf4j
@Service
@RequiredArgsConstructor
public class SykemeldingConsumer {

    public static final String SYNT_SYKEMELDING_URL = "/api/v1/synt-sykemelding";
    public static final String DETALJERT_SYKEMELDING_URL = "/api/v1/sykemeldinger";

    private final RestTemplate restTemplate;
    private final ProvidersProps providersProps;

    @Timed(name = "providers", tags = { "operation", "syntsykemelding_opprett" })
    public ResponseEntity<String> postSyntSykemelding(SyntSykemeldingRequest sykemeldingRequest) {

        String callId = getNavCallId();
        log.info("Synt Sykemelding sendt, callId: {}, consumerId: {}", callId, CONSUMER);

        return restTemplate.exchange(
                RequestEntity.post(URI.create(providersProps.getSyntSykemelding().getUrl() + SYNT_SYKEMELDING_URL))
                        .body(sykemeldingRequest),
                String.class);
    }

    @Timed(name = "providers", tags = { "operation", "detaljertsykemelding_opprett" })
    public ResponseEntity<String> postDetaljertSykemelding(DetaljertSykemeldingRequest detaljertSykemeldingRequest) {

        String callId = getNavCallId();
        log.info("Detaljert Sykemelding sendt, callId: {}, consumerId: {}", callId, CONSUMER);

        return restTemplate.exchange(
                RequestEntity.post(URI.create(providersProps.getDetaljertSykemelding().getUrl() + DETALJERT_SYKEMELDING_URL))
                        .body(detaljertSykemeldingRequest),
                String.class);
    }

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID().toString());
    }
}
