package no.nav.registre.arena.core.consumer.rs;

import io.micrometer.core.annotation.Timed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.List;

//import io.micrometer.core.annotation.Timed;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.core.ParameterizedTypeReference;
//import org.springframework.http.RequestEntity;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.util.UriTemplate;
//
//import java.util.List;
//
public class HodejegerenConsumer {

    private static final ParameterizedTypeReference<List<String>> RESPONSE_TYPE = new ParameterizedTypeReference<List<String>>() {
    };

    private static final int MINIMUM_ALDER = 16;

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate finnLevendeIdenterOverAlderUrl;

 public HodejegerenConsumer(@Value("${testnorge-hodejegeren.rest-api.url}") String hodejegerenServerUrl) {
        this.finnLevendeIdenterOverAlderUrl = new UriTemplate(hodejegerenServerUrl +
                "/v1/levende-identer-over-alder/{avspillergruppeId}?minimumAlder={minimumAlder}");
    }

    @Timed(value = "inst.resource.latency", extraTags = { "operation", "hodejegeren" })
    public List<String> finnLevendeIdenterOverAlder(Long avspillergruppeId) {
        RequestEntity getRequest = RequestEntity.get(finnLevendeIdenterOverAlderUrl.expand(avspillergruppeId.toString(), MINIMUM_ALDER)).build();
        return restTemplate.exchange(getRequest, RESPONSE_TYPE).getBody();
    }
}
