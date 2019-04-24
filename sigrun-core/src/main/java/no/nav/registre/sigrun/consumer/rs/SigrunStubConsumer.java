package no.nav.registre.sigrun.consumer.rs;

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
import java.util.Map;

@Component
@Slf4j
public class SigrunStubConsumer {

    private static final ParameterizedTypeReference<List<String>> RESPONSE_TYPE = new ParameterizedTypeReference<List<String>>() {
    };

    @Autowired
    private RestTemplate restTemplate;

    private String sigrunBaseUrl;

    public SigrunStubConsumer(@Value("${sigrunstub.url}") String sigrunServerUrl) {
        this.sigrunBaseUrl = sigrunServerUrl;
    }

    @Timed(value = "testnorge-sigrun.resource.latency", extraTags = { "operation", "sigrun-skd-stub" })
    public List<String> hentEksisterendePersonidentifikatorer(String miljoe) {
        UriTemplate hentFnrUrl = new UriTemplate(String.format(sigrunBaseUrl, miljoe) + "testdata/hentPersonidentifikatorer");
        RequestEntity getRequest = RequestEntity.get(hentFnrUrl.expand()).build();
        ResponseEntity<List<String>> response = restTemplate.exchange(getRequest, RESPONSE_TYPE);

        List<String> identer = new ArrayList<>();

        if (response.getBody() != null) {
            identer.addAll(response.getBody());
        } else {
            log.error("SigrunStubConsumer.hentEksisterendePersonidentifikatorer: Kunne ikke hente response body fra sigrun-skd-stub: NullPointerException");
        }

        return identer;
    }

    @Timed(value = "testnorge-sigrun.resource.latency", extraTags = { "operation", "sigrun-skd-stub" })
    public ResponseEntity sendDataTilSigrunstub(List<Map<String, Object>> meldinger, String testdataEier, String miljoe) {
        UriTemplate sendDataUrl = new UriTemplate(String.format(sigrunBaseUrl, miljoe) + "testdata/opprettBolk");
        RequestEntity postRequest = RequestEntity.post(sendDataUrl.expand()).header("testdataEier", testdataEier).body(meldinger);
        return restTemplate.exchange(postRequest, RESPONSE_TYPE);
    }
}
