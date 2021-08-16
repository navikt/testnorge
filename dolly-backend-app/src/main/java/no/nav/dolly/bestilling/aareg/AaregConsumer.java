package no.nav.dolly.bestilling.aareg;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.aareg.domain.AaregOpprettRequest;
import no.nav.dolly.bestilling.aareg.domain.AaregResponse;
import no.nav.dolly.bestilling.aareg.domain.ArbeidsforholdResponse;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.properties.ProvidersProps;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AaregConsumer {

    private static final String AAREGDATA_URL = "/api/v1/arbeidsforhold";
    private static final String OPPRETT_ARBEIDSFORHOLD = "%s" + AAREGDATA_URL;
    private static final String HENT_ARBEIDSFORHOLD = "%s" + AAREGDATA_URL + "?ident=%s&miljoe=%s";
    private static final String SLETT_ARBEIDSFORHOLD = "%s" + AAREGDATA_URL + "?ident=%s";

    private final RestTemplate restTemplate;
    private final ProvidersProps providersProps;

    @Timed(name = "providers", tags = { "operation", "aareg_opprettArbeidforhold" })
    public AaregResponse opprettArbeidsforhold(AaregOpprettRequest request) {

        return restTemplate.exchange(
                RequestEntity.post(URI.create(format(OPPRETT_ARBEIDSFORHOLD, providersProps.getAaregdata().getUrl())))
                        .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                        .header(HEADER_NAV_CALL_ID, getNavCallId())
                        .body(request), AaregResponse.class).getBody();
    }

    @Timed(name = "providers", tags = { "operation", "aareg_getArbeidforhold" })
    public List<ArbeidsforholdResponse> hentArbeidsforhold(String ident, String miljoe) {

        try {
            ResponseEntity<ArbeidsforholdResponse[]> response = restTemplate.exchange(
                    RequestEntity.get(URI.create(format(HENT_ARBEIDSFORHOLD, providersProps.getAaregdata().getUrl(), ident, miljoe)))
                            .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                            .header(HEADER_NAV_CALL_ID, getNavCallId())
                            .build(), ArbeidsforholdResponse[].class);

            return response.hasBody() ? new ArrayList<>(List.of(response.getBody())) : emptyList();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return emptyList();
            } else {
                throw e;
            }
        }
    }

    @Timed(name = "providers", tags = { "operation", "aareg_deleteArbeidsforhold" })
    public AaregResponse slettArbeidsforholdFraAlleMiljoer(String ident) {
        RequestEntity deleteRequest =
                RequestEntity.delete(URI.create(format(SLETT_ARBEIDSFORHOLD, providersProps.getAaregdata().getUrl(), ident)))
                        .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                        .header(HEADER_NAV_CALL_ID, getNavCallId())
                        .build();
        return restTemplate.exchange(deleteRequest, AaregResponse.class).getBody();
    }

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID().toString());
    }
}