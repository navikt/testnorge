package no.nav.registre.hodejegeren.consumer;

import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.net.URI;
import java.util.List;

@Component
public class TpsSyntetisererenConsumer {

    private static final ParameterizedTypeReference<List<RsMeldingstype>> RESPONSE_TYPE = new ParameterizedTypeReference<List<RsMeldingstype>>() {
    };

    private RestTemplate restTemplate;
    private UriTemplate uriTemplate;

    public TpsSyntetisererenConsumer(RestTemplateBuilder restTemplateBuilder,
                                     @Value("${tps-syntetisereren.rest-api.url}") String serverUrl
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.uriTemplate = new UriTemplate(serverUrl + "/generate?endringskode={endringskode}&antallMeldinger={antall}&service=orkestratoren");
    }

    public List<RsMeldingstype> getSyntetiserteSkdmeldinger(String endringskode, Integer antallMeldinger) {
        URI url = uriTemplate.expand(endringskode, antallMeldinger);
        RequestEntity getRequest = RequestEntity.get(url).build();
        return restTemplate.exchange(getRequest, RESPONSE_TYPE).getBody();
    }
}
