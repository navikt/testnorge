package no.nav.registre.skd.consumer;

import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.net.URI;
import java.util.List;

import no.nav.registre.skd.skdmelding.RsMeldingstype;

@Component
public class TpsSyntetisererenConsumer {

    private static final ParameterizedTypeReference<List<RsMeldingstype>> RESPONSE_TYPE = new ParameterizedTypeReference<List<RsMeldingstype>>() {
    };

    private String serverUrl;

    private RestTemplate restTemplate;

    public TpsSyntetisererenConsumer(RestTemplateBuilder restTemplateBuilder,
            @Value("${syntrest.rest.api.url}") String syntrestServerUrl) {
        this.restTemplate = restTemplateBuilder.build();
        this.serverUrl = syntrestServerUrl;
    }

    @Timed(value = "skd.resource.latency", extraTags = { "operation", "tps-syntetisereren" })
    public List<RsMeldingstype> getSyntetiserteSkdmeldinger(String endringskode, Integer antallMeldinger) {
        UriTemplate uriTemplate = new UriTemplate(serverUrl + "/v1/generateTps/{antallMeldinger}/{endringskode}");
        URI url = uriTemplate.expand(antallMeldinger, endringskode);
        RequestEntity getRequest = RequestEntity.get(url).build();
        return restTemplate.exchange(getRequest, RESPONSE_TYPE).getBody();
    }
}