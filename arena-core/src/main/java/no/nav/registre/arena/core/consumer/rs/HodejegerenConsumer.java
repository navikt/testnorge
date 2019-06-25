package no.nav.registre.arena.core.consumer.rs;


import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.ArrayList;
import java.util.List;


@Component
@Slf4j
public class HodejegerenConsumer {
    private static final int MINIMUM_ALDER = 16;
    private static final ParameterizedTypeReference<List<String>> RESPONSE_TYPE = new ParameterizedTypeReference<List<String>>(){};

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate hentLevendeIdenterOverAlderUrl;




    public HodejegerenConsumer(@Value("{testnorge-hodejegeren.rest-api.url}") String hodejegerenServerUrl) {

        this.hentLevendeIdenterOverAlderUrl = new UriTemplate(hodejegerenServerUrl + "/api/v1/levende-identer-over-alder/{avspillergruppeId}?minimumAlder=" + MINIMUM_ALDER);

    }

    @Timed(value = "arena.resource.latency", extraTags = {"operation", "hodejegeren"})
    public List<String> finnLevendeIdenterOverAlder(Long avspillergruppeId) {
        // List<String> levendeIdenterOverAlder = new ArrayList<>();

        RequestEntity getRequest = RequestEntity.get(hentLevendeIdenterOverAlderUrl.expand(avspillergruppeId.toString())).build();
        return new ArrayList<>(restTemplate.exchange(getRequest, RESPONSE_TYPE).getBody());
    }

}
