package no.nav.dolly.bestilling.inntektsmelding;

import static java.lang.String.format;
import static no.nav.dolly.domain.CommonKeys.CONSUMER;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CONSUMER_ID;

import java.net.URI;
import java.util.UUID;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.inntektsmelding.domain.Inntektsmelding;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.properties.ProvidersProps;

@Slf4j
@Service
@RequiredArgsConstructor
public class InntektsmeldingConsumer {

    private static final String INNTEKTSMELDING_URL = "/api/v1/altinnInntekt";
    private static final String POST_FMT_BLD = INNTEKTSMELDING_URL + "/enkeltident";

    private final RestTemplate restTemplate;
    private final ProvidersProps providersProps;

    @Timed(name = "providers", tags = { "operation", "inntektsmelding_opprett" })
    public ResponseEntity postInntektsmelding(Inntektsmelding inntekstsmelding) {
        return restTemplate.exchange(
                RequestEntity.post(URI.create(providersProps.getInntektsmelding().getUrl() + POST_FMT_BLD))
                        .header(HEADER_NAV_CALL_ID, getNavCallId())
                        .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                        .body(inntekstsmelding), String.class);
    }

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID().toString());
    }
}
