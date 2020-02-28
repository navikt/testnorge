package no.nav.registre.spion.consumer.rs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.List;

import no.nav.registre.spion.consumer.rs.response.aareg.AaregResponse;

@Component
public class AaregConsumer {

    private final RestTemplate restTemplate;
    private final UriTemplate hentAlleIdenterMedArbeidsforholdUrl;
    private final UriTemplate hentArbeidsforholdUrl;

    public AaregConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${testnorge.rest-api.aareg}") String serverUrl
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.hentAlleIdenterMedArbeidsforholdUrl =
                new UriTemplate(serverUrl + "/v1/ident/avspillergruppe/{avspillergruppeId}?miljoe={miljoe}");
        this.hentArbeidsforholdUrl = new UriTemplate(serverUrl + "/v1/ident/{ident}?miljoe={miljoe}");
    }

    public List<String> hentAlleIdenterMedArbeidsforhold(long avspillergruppeId, String miljoe) {
        var requestEntity = RequestEntity.get(hentAlleIdenterMedArbeidsforholdUrl.expand(avspillergruppeId, miljoe)).build();
        return restTemplate.exchange(requestEntity, new ParameterizedTypeReference<List<String>>() {
        }).getBody();
    }

    public AaregResponse[] hentArbeidsforholdTilIdent(String ident, String miljoe) {
        var requestEntity = RequestEntity.get(hentArbeidsforholdUrl.expand(ident, miljoe)).build();
        var response = restTemplate.exchange(requestEntity, AaregResponse[].class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new HttpServerErrorException(response.getStatusCode());
        }
        return response.getBody();
    }
}