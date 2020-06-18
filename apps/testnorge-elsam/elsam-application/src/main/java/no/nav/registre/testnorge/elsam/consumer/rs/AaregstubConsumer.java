package no.nav.registre.testnorge.elsam.consumer.rs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.List;

import no.nav.registre.testnorge.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.elsam.consumer.rs.response.aareg.AaregResponse;

@Component
@DependencyOn("testnorge-aaregstub")
public class AaregstubConsumer {

    private final RestTemplate restTemplate;
    private final UriTemplate hentAlleIdenterIStubUrl;
    private final UriTemplate hentArbeidsforholdUrl;

    public AaregstubConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${testnorge.rest-api.aaregstub}") String serverUrl
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.hentAlleIdenterIStubUrl = new UriTemplate(serverUrl + "/v1/hentAlleArbeidstakere");
        this.hentArbeidsforholdUrl = new UriTemplate(serverUrl + "/v1/hentIdentMedArbeidsforhold/{ident}");
    }

    public List<String> hentAlleIdenterIStub() {
        var requestEntity = RequestEntity.get(hentAlleIdenterIStubUrl.expand()).build();
        return restTemplate.exchange(requestEntity, new ParameterizedTypeReference<List<String>>() {
        }).getBody();
    }

    public AaregResponse hentArbeidsforholdTilIdent(String ident) {
        var requestEntity = RequestEntity.get(hentArbeidsforholdUrl.expand(ident)).build();
        var response = restTemplate.exchange(requestEntity, AaregResponse.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new HttpServerErrorException(response.getStatusCode());
        }
        return response.getBody();
    }
}
