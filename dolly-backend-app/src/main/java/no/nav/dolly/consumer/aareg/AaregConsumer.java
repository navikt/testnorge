package no.nav.dolly.consumer.aareg;

import static java.lang.String.format;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.security.sts.StsOidcService.getUserIdToken;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import lombok.RequiredArgsConstructor;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

import no.nav.dolly.domain.resultset.aareg.RsAaregOppdaterRequest;
import no.nav.dolly.domain.resultset.aareg.RsAaregOpprettRequest;
import no.nav.dolly.domain.resultset.aareg.RsAaregResponse;
import no.nav.dolly.properties.ProvidersProps;

@Component
@RequiredArgsConstructor
public class AaregConsumer {

    private static final String CONSUMER = "Dolly";
    private static final String AAREGDATA_URL = "/api/v1/arbeidsforhold";
    private static final String OPPRETT_ARBEIDSFORHOLD = "%s" + AAREGDATA_URL;
    private static final String OPPDATER_ARBEIDSFORHOLD = "%s" + AAREGDATA_URL;
    private static final String HENT_ARBEIDSFORHOLD = "%s" + AAREGDATA_URL + "?ident=%s&miljoe=%s";
    private static final String SLETT_ARBEIDSFORHOLD = "%s" + AAREGDATA_URL + "?ident=%s";

    private final RestTemplate restTemplate;
    private final ProvidersProps providersProps;

    public RsAaregResponse opprettArbeidsforhold(RsAaregOpprettRequest request) {
        RequestEntity postRequest =
                RequestEntity.post(URI.create(format(OPPRETT_ARBEIDSFORHOLD, providersProps.getAaregdata().getUrl())))
                        .header(AUTHORIZATION, getUserIdToken())
                        .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                        .header(HEADER_NAV_CALL_ID, getNavCallId())
                        .body(request);
        return restTemplate.exchange(postRequest, RsAaregResponse.class).getBody();
    }

    public RsAaregResponse oppdaterArbeidsforhold(RsAaregOppdaterRequest request) {
        RequestEntity putRequest =
                RequestEntity.put(URI.create(format(OPPDATER_ARBEIDSFORHOLD, providersProps.getAaregdata().getUrl())))
                        .header(AUTHORIZATION, getUserIdToken())
                        .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                        .header(HEADER_NAV_CALL_ID, getNavCallId())
                        .body(request);
        return restTemplate.exchange(putRequest, RsAaregResponse.class).getBody();
    }

    public ResponseEntity<Map[]> hentArbeidsforhold(String ident, String miljoe) {
        RequestEntity getRequest =
                RequestEntity.get(URI.create(format(HENT_ARBEIDSFORHOLD, providersProps.getAaregdata().getUrl(), ident, miljoe)))
                        .header(AUTHORIZATION, getUserIdToken())
                        .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                        .header(HEADER_NAV_CALL_ID, getNavCallId())
                        .build();
        return restTemplate.exchange(getRequest, Map[].class);
    }

    public RsAaregResponse slettArbeidsforholdFraAlleMiljoer(String ident) {
        RequestEntity deleteRequest =
                RequestEntity.delete(URI.create(format(SLETT_ARBEIDSFORHOLD, providersProps.getAaregdata().getUrl(), ident)))
                        .header(AUTHORIZATION, getUserIdToken())
                        .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                        .header(HEADER_NAV_CALL_ID, getNavCallId())
                        .build();
        return restTemplate.exchange(deleteRequest, RsAaregResponse.class).getBody();
    }

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID().toString());
    }
}