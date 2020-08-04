package no.nav.dolly.bestilling.aareg;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static no.nav.dolly.domain.CommonKeys.CONSUMER;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.security.sts.StsOidcService.getUserIdToken;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.aareg.domain.AaregOpprettRequest;
import no.nav.dolly.bestilling.aareg.domain.AaregResponse;
import no.nav.dolly.bestilling.aareg.domain.ArbeidsforholdResponse;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.properties.ProvidersProps;

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
        RequestEntity postRequest =
                RequestEntity.post(URI.create(format(OPPRETT_ARBEIDSFORHOLD, providersProps.getAaregdata().getUrl())))
                        .header(AUTHORIZATION, getUserIdToken())
                        .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                        .header(HEADER_NAV_CALL_ID, getNavCallId())
                        .body(request);
        return restTemplate.exchange(postRequest, AaregResponse.class).getBody();
    }

    @Timed(name = "providers", tags = { "operation", "aareg_getArbeidforhold" })
    public List<ArbeidsforholdResponse> hentArbeidsforhold(String ident, String miljoe) {

            ResponseEntity<ArbeidsforholdResponse[]> response =
                    restTemplate.exchange(
                            RequestEntity.get(URI.create(format(HENT_ARBEIDSFORHOLD, providersProps.getAaregdata().getUrl(), ident, miljoe)))
                                    .header(AUTHORIZATION, getUserIdToken())
                                    .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                                    .header(HEADER_NAV_CALL_ID, getNavCallId())
                                    .build(), ArbeidsforholdResponse[].class);

            return response.hasBody() ? newArrayList(response.getBody()) : emptyList();
    }

    @Timed(name = "providers", tags = { "operation", "aareg_deleteArbeidsforhold" })
    public AaregResponse slettArbeidsforholdFraAlleMiljoer(String ident) {
        RequestEntity deleteRequest =
                RequestEntity.delete(URI.create(format(SLETT_ARBEIDSFORHOLD, providersProps.getAaregdata().getUrl(), ident)))
                        .header(AUTHORIZATION, getUserIdToken())
                        .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                        .header(HEADER_NAV_CALL_ID, getNavCallId())
                        .build();
        return restTemplate.exchange(deleteRequest, AaregResponse.class).getBody();
    }

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID().toString());
    }
}