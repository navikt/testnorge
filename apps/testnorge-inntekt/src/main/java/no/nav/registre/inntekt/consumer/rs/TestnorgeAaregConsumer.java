package no.nav.registre.inntekt.consumer.rs;

import static no.nav.registre.inntekt.consumer.rs.ConsumerUtils.CALL_ID_NAME;
import static no.nav.registre.inntekt.consumer.rs.ConsumerUtils.CONSUMER_ID_NAME;
import static no.nav.registre.inntekt.consumer.rs.ConsumerUtils.NAV_CALL_ID;
import static no.nav.registre.inntekt.consumer.rs.ConsumerUtils.NAV_CONSUMER_ID;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.inntekt.utils.ArbeidsforholdMappingUtil;

import no.nav.testnav.libs.domain.dto.aordningen.arbeidsforhold.Arbeidsforhold;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TestnorgeAaregConsumer {

    private RestTemplate restTemplate;

    private UriTemplate hentIdenterFraAaregUrl;
    private UriTemplate hentArbeidsforholdFraAaregUrl;

    public TestnorgeAaregConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${testnorge-aareg.rest.api.url}") String testnorgeAaregServerUrl
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.hentIdenterFraAaregUrl = new UriTemplate(testnorgeAaregServerUrl + "/v1/ident/avspillergruppe/{avspillergruppeId}?miljoe={miljoe}&validerMotAareg=true");
        this.hentArbeidsforholdFraAaregUrl = new UriTemplate(testnorgeAaregServerUrl + "/v1/ident/{ident}?miljoe={miljoe}");
    }

    public List<String> hentIdenterIAvspillergruppeMedArbeidsforhold(
            Long avspillergruppeId,
            String miljoe
    ) {
        var getRequest = RequestEntity.get(hentIdenterFraAaregUrl.expand(avspillergruppeId, miljoe)).build();
        return restTemplate.exchange(getRequest, new ParameterizedTypeReference<List<String>>() {
        }).getBody();
    }

    public List<Arbeidsforhold> hentArbeidsforholdTilIdentIMiljoe(String ident, String miljoe) {
       var getRequest = RequestEntity.get(hentArbeidsforholdFraAaregUrl.expand(ident, miljoe))
               .header(CALL_ID_NAME, NAV_CALL_ID)
               .header(CONSUMER_ID_NAME, NAV_CONSUMER_ID)
               .build();
       List<Arbeidsforhold> response = null;
       try {
           var tmp = restTemplate.exchange(getRequest, new ParameterizedTypeReference<List<JsonNode>>() {
           }).getBody();
           assert tmp != null;
           response = tmp.stream().map(ArbeidsforholdMappingUtil::mapToArbeidsforhold).toList();
       } catch (HttpStatusCodeException e) {
           log.error("Kunne ikke hente arbeidsforhold til ident " + ident);
       } catch (Exception e) {
           log.error("Uventet feil ved henting av arbeidsforhold.", e);
       }

       return response;
    }
}
