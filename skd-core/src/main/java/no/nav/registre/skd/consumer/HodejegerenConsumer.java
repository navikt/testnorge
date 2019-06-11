package no.nav.registre.skd.consumer;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.nav.registre.skd.service.Endringskoder;

@Component
@Slf4j
public class HodejegerenConsumer {

    private static final ParameterizedTypeReference<List<String>> RESPONSE_TYPE_LIST = new ParameterizedTypeReference<List<String>>() {
    };
    private static final ParameterizedTypeReference<Map<String, String>> RESPONSE_TYPE_MAP = new ParameterizedTypeReference<Map<String, String>>() {
    };

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate levendeIdenterUrl;
    private UriTemplate doedeIdenterUrl;
    private UriTemplate gifteIdenterUrl;
    private UriTemplate statusQuoUrl;

    public HodejegerenConsumer(@Value("${testnorge-hodejegeren.rest-api.url}") String hodejegerenServerUrl) {
        this.levendeIdenterUrl = new UriTemplate(hodejegerenServerUrl + "/v1/alle-levende-identer/{avspillergruppeId}");
        this.doedeIdenterUrl = new UriTemplate(hodejegerenServerUrl + "/v1/doede-identer/{avspillergruppeId}");
        this.gifteIdenterUrl = new UriTemplate(hodejegerenServerUrl + "/v1/gifte-identer/{avspillergruppeId}");
        this.statusQuoUrl = new UriTemplate(hodejegerenServerUrl + "/v1/status-quo?endringskode={endringskode}&miljoe={miljoe}&fnr={fnr}");
    }

    @Timed(value = "skd.resource.latency", extraTags = { "operation", "hodejegeren" })
    public List<String> finnLevendeIdenter(Long avspillergruppeId) {
        RequestEntity getRequest = RequestEntity.get(levendeIdenterUrl.expand(avspillergruppeId.toString())).build();
        List<String> levendeIdenter = new ArrayList<>();
        ResponseEntity<List<String>> response = restTemplate.exchange(getRequest, RESPONSE_TYPE_LIST);

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.warn("Fikk statuskode {} fra testnorge-hodejegeren", response.getStatusCode());
        }

        if (response.getBody() != null) {
            levendeIdenter.addAll(response.getBody());
        } else {
            log.error("HodejegerenConsumer.finnLevendeIdenter: Kunne ikke hente response body fra Hodejegeren: NullPointerException");
        }

        return levendeIdenter;
    }

    @Timed(value = "skd.resource.latency", extraTags = { "operation", "hodejegeren" })
    public List<String> finnDoedeOgUtvandredeIdenter(Long avspillergruppeId) {
        RequestEntity getRequest = RequestEntity.get(doedeIdenterUrl.expand(avspillergruppeId.toString())).build();
        List<String> doedeOgUtvandredeIdenter = new ArrayList<>();
        ResponseEntity<List<String>> response = restTemplate.exchange(getRequest, RESPONSE_TYPE_LIST);

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.warn("Fikk statuskode {} fra testnorge-hodejegeren", response.getStatusCode());
        }

        if (response.getBody() != null) {
            doedeOgUtvandredeIdenter.addAll(response.getBody());
        } else {
            log.error("HodejegerenConsumer.finnDoedeOgUtvandredeIdenter: Kunne ikke hente response body fra Hodejegeren: NullPointerException");
        }

        return doedeOgUtvandredeIdenter;
    }

    @Timed(value = "skd.resource.latency", extraTags = { "operation", "hodejegeren" })
    public List<String> finnGifteIdenter(Long avspillergruppeId) {
        RequestEntity getRequest = RequestEntity.get(gifteIdenterUrl.expand(avspillergruppeId.toString())).build();
        List<String> gifteIdenter = new ArrayList<>();
        ResponseEntity<List<String>> response = restTemplate.exchange(getRequest, RESPONSE_TYPE_LIST);

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.warn("Fikk statuskode {} fra testnorge-hodejegeren", response.getStatusCode());
        }

        if (response.getBody() != null) {
            gifteIdenter.addAll(response.getBody());
        } else {
            log.error("HodejegerenConsumer.finnGifteIdenter: Kunne ikke hente response body fra Hodejegeren: NullPointerException");
        }

        return gifteIdenter;
    }

    @Timed(value = "skd.resource.latency", extraTags = { "operation", "hodejegeren" })
    public Map<String, String> getStatusQuoFraEndringskode(Endringskoder endringskode, String miljoe, String fnr) {
        RequestEntity getRequest = RequestEntity.get(statusQuoUrl.expand(endringskode.toString(), miljoe, fnr)).build();
        Map<String, String> statusQuo = new HashMap<>();
        ResponseEntity<Map<String, String>> response;
        try {
            response = restTemplate.exchange(getRequest, RESPONSE_TYPE_MAP);
        } catch (HttpStatusCodeException e) {
            log.info("Kunne ikke hente status quo på ident {}", fnr, e);
            return new HashMap<>();
        }

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.warn("Fikk statuskode {} fra testnorge-hodejegeren", response.getStatusCode());
        }

        if (response.getBody() != null) {
            statusQuo.putAll(response.getBody());
        } else {
            log.error("HodejegerenConsumer.getStatusQuoFraEndringskode: Kunne ikke hente response body fra Hodejegeren: NullPointerException");
        }

        return statusQuo;
    }
}
