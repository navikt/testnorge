package no.nav.registre.arena.core.consumer.rs;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class HodejegerenConsumer {

    private int MINIMUM_ALDER = 16;
    private static final ParameterizedTypeReference<List<String>> RESPONSE_TYPE = new ParameterizedTypeReference<List<String>>() {
    };

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate hentLevendeIdenterOverAlderUrl;

    public HodejegerenConsumer(@Value("${testnorge-hodejegeren.rest-api.url}") String hodejegerenServerUrl) {
        this.hentLevendeIdenterOverAlderUrl = new UriTemplate(hodejegerenServerUrl +
                "/v1/levende-identer-over-alder/{avspillergruppeId}?minimumAlder=" + MINIMUM_ALDER);
    }

    @Timed(value = "arena.resource.latency", extraTags = {"operation", "hodejegeren"})
    public List<String> finnLevendeIdenterOverAlder(Long avspillergruppeId) {

        RequestEntity getRequest = RequestEntity.get(hentLevendeIdenterOverAlderUrl.expand(avspillergruppeId.toString())).build();
        ResponseEntity<List<String>> response = restTemplate.exchange(getRequest, RESPONSE_TYPE);
        List<String> levendeIdenter = new ArrayList<>();

        if (response.getBody() == null) {
            log.error("HodejegerenConsumer.finnLevendeIdenterOverAlder: Kunne ikke hente response body fra Hodejegeren: NullPointerException");
        } else {
            levendeIdenter.addAll(response.getBody());
        }

        return levendeIdenter;
    }
}
