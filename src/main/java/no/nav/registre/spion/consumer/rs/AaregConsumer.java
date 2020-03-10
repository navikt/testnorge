package no.nav.registre.spion.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.List;

import no.nav.registre.spion.consumer.rs.response.aareg.AaregResponse;

@Slf4j
@Component
public class AaregConsumer {

    private final RestTemplate restTemplate;
    private final String aaregUrl;

    public AaregConsumer(
            RestTemplate restTemplate,
            @Value("${testnorge.rest-api.aareg}") String serverUrl
    ) {
        this.restTemplate = restTemplate;
        this.aaregUrl = serverUrl;
    }

    public List<String> hentAlleIdenterMedArbeidsforhold(long avspillergruppeId, String miljoe) {
        UriTemplate uriTemplate = new UriTemplate(aaregUrl
                + "/v1/ident/avspillergruppe/{avspillergruppeId}?miljoe={miljoe}&validerMotAareg=true");
        var getRequest = RequestEntity.get(uriTemplate.expand(avspillergruppeId, miljoe)).build();

        try{
            return restTemplate.exchange(getRequest, new ParameterizedTypeReference<List<String>>() {}).getBody();
        }catch(HttpStatusCodeException e){
            log.error("Kunne ikke hente identer med arbeidsforhold fra aareg.");
            throw e;
        }
    }

    public List<AaregResponse> hentArbeidsforholdTilIdent(String ident, String miljoe) {
        UriTemplate uriTemplate = new UriTemplate(aaregUrl
                + "/v1/ident/{ident}?miljoe={miljoe}");
        var getRequest = RequestEntity.get(uriTemplate.expand(ident, miljoe)).build();

        try{
            return restTemplate.exchange(getRequest, new ParameterizedTypeReference<List<AaregResponse>>() {}).getBody();
        }catch(HttpStatusCodeException e){
            log.error("Kunne ikke hente arbeidsforhold for ident fra aareg");
            throw e;
        }
    }
}