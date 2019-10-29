package no.nav.dolly.consumer.aareg;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.Map;

import no.nav.dolly.domain.resultset.aareg.RsAaregOppdaterRequest;
import no.nav.dolly.domain.resultset.aareg.RsAaregOpprettRequest;
import no.nav.dolly.domain.resultset.aareg.RsAaregResponse;

@Component
public class TestnorgeAaregConsumer {

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
        RequestEntity postRequest = RequestEntity.post(opprettArbeidsforholdUrl.expand()).body(request);
        return restTemplate.exchange(postRequest, RsAaregResponse.class).getBody();
    }

    public RsAaregResponse oppdaterArbeidsforhold(RsAaregOppdaterRequest request) {
        RequestEntity putRequest = RequestEntity.put(oppdaterArbeidsforholdUrl.expand()).body(request);
        return restTemplate.exchange(putRequest, RsAaregResponse.class).getBody();
    }

    public ResponseEntity<Map[]> hentArbeidsforhold(String ident, String miljoe) {
        RequestEntity getRequest = RequestEntity.get(hentArbeidsforholdUrl.expand(ident, miljoe)).build();
        return restTemplate.exchange(getRequest, Map[].class);
    }

    public Map<String, String> slettArbeidsforhold(String ident) {
        RequestEntity deleteRequest = RequestEntity.delete(slettArbeidsforholdUrl.expand(ident)).build();
        return restTemplate.exchange(deleteRequest, new ParameterizedTypeReference<Map<String, String>>() {
        }).getBody();
    }
}
