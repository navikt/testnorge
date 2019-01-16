package no.nav.registre.skd.consumer;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.nav.registre.skd.consumer.requests.LagreITpsfRequest;
import no.nav.registre.skd.service.Endringskoder;
import no.nav.registre.skd.skdmelding.RsMeldingstype;

@Component
@Slf4j
public class HodejegerenConsumer {

    private static final ParameterizedTypeReference<List<String>> RESPONSE_TYPE_LIST_STRING = new ParameterizedTypeReference<List<String>>() {
    };
    private static final ParameterizedTypeReference<List<Long>> RESPONSE_TYPE_LIST_LONG = new ParameterizedTypeReference<List<Long>>() {
    };
    private static final ParameterizedTypeReference<Map<String, String>> RESPONSE_TYPE_MAP = new ParameterizedTypeReference<Map<String, String>>() {
    };

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate levendeIdenterUrl;
    private UriTemplate doedeIdenterUrl;
    private UriTemplate gifteIdenterUrl;
    private UriTemplate statusQuoUrl;
    private UriTemplate lagreITpsfUrl;

    public HodejegerenConsumer(@Value("${testnorge-hodejegeren.rest-api.url}") String hodejegerenServerUrl) {
        this.levendeIdenterUrl = new UriTemplate(hodejegerenServerUrl + "/v1/levende-identer/{avspillergruppeId}");
        this.doedeIdenterUrl = new UriTemplate(hodejegerenServerUrl + "/v1/doede-identer/{avspillergruppeId}");
        this.gifteIdenterUrl = new UriTemplate(hodejegerenServerUrl + "/v1/gifte-identer/{avspillergruppeId}");
        this.statusQuoUrl = new UriTemplate(hodejegerenServerUrl + "/v1/status-quo/{endringskode}/{miljoe}/{fnr}");
        this.lagreITpsfUrl = new UriTemplate(hodejegerenServerUrl + "/v1/lagre-tpsf");
    }

    public List<String> finnLevendeIdenter(Long avspillergruppeId) {
        RequestEntity getRequest = RequestEntity.get(levendeIdenterUrl.expand(avspillergruppeId.toString())).build();
        List<String> levendeIdenter = new ArrayList<>();
        ResponseEntity<List<String>> response = restTemplate.exchange(getRequest, RESPONSE_TYPE_LIST_STRING);

        if (response != null && response.getBody() != null) {
            levendeIdenter.addAll(response.getBody());
        } else {
            log.error("HodejegerenConsumer.finnLevendeIdenter: Kunne ikke hente response body fra Hodejegeren: NullPointerException");
        }

        return levendeIdenter;
    }

    public List<String> finnDoedeOgUtvandredeIdenter(Long avspillergruppeId) {
        RequestEntity getRequest = RequestEntity.get(doedeIdenterUrl.expand(avspillergruppeId.toString())).build();
        List<String> doedeOgUtvandredeIdenter = new ArrayList<>();
        ResponseEntity<List<String>> response = restTemplate.exchange(getRequest, RESPONSE_TYPE_LIST_STRING);

        if (response != null && response.getBody() != null) {
            doedeOgUtvandredeIdenter.addAll(response.getBody());
        } else {
            log.error("HodejegerenConsumer.finnDoedeOgUtvandredeIdenter: Kunne ikke hente response body fra Hodejegeren: NullPointerException");
        }

        return doedeOgUtvandredeIdenter;
    }

    public List<String> finnGifteIdenter(Long avspillergruppeId) {
        RequestEntity getRequest = RequestEntity.get(gifteIdenterUrl.expand(avspillergruppeId.toString())).build();
        List<String> gifteIdenter = new ArrayList<>();
        ResponseEntity<List<String>> response = restTemplate.exchange(getRequest, RESPONSE_TYPE_LIST_STRING);

        if (response != null && response.getBody() != null) {
            gifteIdenter.addAll(response.getBody());
        } else {
            log.error("HodejegerenConsumer.finnGifteIdenter: Kunne ikke hente response body fra Hodejegeren: NullPointerException");
        }

        return gifteIdenter;
    }

    public Map<String, String> getStatusQuoFraEndringskode(Endringskoder endringskode, String miljoe, String fnr) {
        RequestEntity getRequest = RequestEntity.get(statusQuoUrl.expand(endringskode.toString(), miljoe, fnr)).build();
        Map<String, String> statusQuo = new HashMap<>();
        ResponseEntity<Map<String, String>> response = restTemplate.exchange(getRequest, RESPONSE_TYPE_MAP);

        if (response != null && response.getBody() != null) {
            statusQuo.putAll(response.getBody());
        } else {
            log.error("HodejegerenConsumer.getStatusQuoFraEndringskode: Kunne ikke hente response body fra Hodejegeren: NullPointerException");
        }

        return statusQuo;
    }

    public List<Long> lagreSkdEndringsmeldingerITpsf(Long avspillergruppeId, List<RsMeldingstype> skdMeldinger) {
        RequestEntity postRequest = RequestEntity.post(lagreITpsfUrl.expand()).body(new LagreITpsfRequest(avspillergruppeId, skdMeldinger));
        List<Long> ids = new ArrayList<>();
        ResponseEntity<List<Long>> response = restTemplate.exchange(postRequest, RESPONSE_TYPE_LIST_LONG);

        if (response != null && response.getBody() != null) {
            ids.addAll(response.getBody());
        } else {
            log.error("HodejegerenConsumer.lagreSkdEndringsmeldingerITpsf: Kunne ikke hente response body fra Hodejegeren: NullPointerException");
        }

        return ids;
    }
}
