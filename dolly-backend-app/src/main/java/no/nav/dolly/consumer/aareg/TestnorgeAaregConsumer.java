package no.nav.dolly.consumer.aareg;

import static java.lang.String.format;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.security.sts.StsOidcService.getUserIdToken;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.Map;
import java.util.UUID;

import no.nav.dolly.domain.resultset.aareg.RsAaregOppdaterRequest;
import no.nav.dolly.domain.resultset.aareg.RsAaregOpprettRequest;
import no.nav.dolly.domain.resultset.aareg.RsAaregResponse;

@Component
public class TestnorgeAaregConsumer {

    private static final String CONSUMER = "Dolly";
    private static final String OPPRETT_ARBEIDSFORHOLD = "/v1/arbeidsforhold";
    private static final String OPPDATER_ARBEIDSFORHOLD = "/v1/arbeidsforhold";
    private static final String HENT_ARBEIDSFORHOLD = "/v1/arbeidsforhold?ident={ident}&miljoe={miljoe}";
    private static final String SLETT_ARBEIDSFORHOLD = "/v1/arbeidsforhold?ident={ident}";

    private final RestTemplate restTemplate;

    private UriTemplate opprettArbeidsforholdUrl;
    private UriTemplate oppdaterArbeidsforholdUrl;
    private UriTemplate hentArbeidsforholdUrl;
    private UriTemplate slettArbeidsforholdUrl;

    public TestnorgeAaregConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${providers.aaregdata.url}") String testnorgeAaregServerUrl
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.opprettArbeidsforholdUrl = new UriTemplate(testnorgeAaregServerUrl + OPPRETT_ARBEIDSFORHOLD);
        this.oppdaterArbeidsforholdUrl = new UriTemplate(testnorgeAaregServerUrl + OPPDATER_ARBEIDSFORHOLD);
        this.hentArbeidsforholdUrl = new UriTemplate(testnorgeAaregServerUrl + HENT_ARBEIDSFORHOLD);
        this.slettArbeidsforholdUrl = new UriTemplate(testnorgeAaregServerUrl + SLETT_ARBEIDSFORHOLD);
    }

    public RsAaregResponse opprettArbeidsforhold(RsAaregOpprettRequest request) {
        RequestEntity postRequest =
                RequestEntity.post(opprettArbeidsforholdUrl.expand())
                        .header(AUTHORIZATION, getUserIdToken())
                        .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                        .header(HEADER_NAV_CALL_ID, getNavCallId())
                        .body(request);
        return restTemplate.exchange(postRequest, RsAaregResponse.class).getBody();
    }

    public RsAaregResponse oppdaterArbeidsforhold(RsAaregOppdaterRequest request) {
        RequestEntity putRequest =
                RequestEntity.put(oppdaterArbeidsforholdUrl.expand())
                        .header(AUTHORIZATION, getUserIdToken())
                        .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                        .header(HEADER_NAV_CALL_ID, getNavCallId())
                        .body(request);
        return restTemplate.exchange(putRequest, RsAaregResponse.class).getBody();
    }

    public ResponseEntity<Map[]> hentArbeidsforhold(String ident, String miljoe) {
        RequestEntity getRequest =
                RequestEntity.get(hentArbeidsforholdUrl.expand(ident, miljoe))
                        .header(AUTHORIZATION, getUserIdToken())
                        .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                        .header(HEADER_NAV_CALL_ID, getNavCallId())
                        .build();
        return restTemplate.exchange(getRequest, Map[].class);
    }

    public Map<String, String> slettArbeidsforholdFraAlleMiljoer(String ident) {
        RequestEntity deleteRequest =
                RequestEntity.delete(slettArbeidsforholdUrl.expand(ident))
                        .header(AUTHORIZATION, getUserIdToken())
                        .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                        .header(HEADER_NAV_CALL_ID, getNavCallId())
                        .build();
        return restTemplate.exchange(deleteRequest, new ParameterizedTypeReference<Map<String, String>>() {
        }).getBody();
    }

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID().toString());
    }
}
