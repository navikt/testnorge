package no.nav.dolly.bestilling.inntektsmelding;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static no.nav.dolly.domain.CommonKeys.CONSUMER;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CONSUMER_ID;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.google.common.collect.Lists;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.inntektsmelding.domain.Inntektsmelding;
import no.nav.dolly.properties.ProvidersProps;

@Slf4j
@Service
@RequiredArgsConstructor
public class InntektsmeldingConsumer {

    private static final String INNTEKTSMELDING_URL = "/api/v1";
    private static final String DELETE_FMT_BLD = INNTEKTSMELDING_URL + "/xxx";
    private static final String POST_FMT_BLD = INNTEKTSMELDING_URL + "/yyy";
    private static final String MILJOER_URL = INNTEKTSMELDING_URL + "/zzz";

    private final RestTemplate restTemplate;
    private final ProvidersProps providersProps;

    public List<String> getMiljoer() {

        try {
            ResponseEntity responseEntity = restTemplate.exchange(
                    RequestEntity.get(URI.create(providersProps.getInntektsmelding().getUrl() + MILJOER_URL))
                            .header(HEADER_NAV_CALL_ID, getNavCallId())
                            .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                            .build(),
                    String[].class);
            return responseEntity.hasBody() ? Lists.newArrayList((String[]) responseEntity.getBody()) : emptyList();

        } catch (RuntimeException e) {

            log.error("Feilet å lese tilgjengelige miljøer fra inntektsmelding. {}", e.getMessage(), e);
            return emptyList();
        }
    }

    public ResponseEntity deleteInntektsmelding(String ident, String environment) {
        return restTemplate.exchange(
                RequestEntity.delete(URI.create(format(DELETE_FMT_BLD, providersProps.getInntektsmelding().getUrl(), ident, environment)))
                        .header(HEADER_NAV_CALL_ID, getNavCallId())
                        .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                        .build(), String.class);
    }

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
