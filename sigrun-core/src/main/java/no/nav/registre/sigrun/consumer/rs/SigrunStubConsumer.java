package no.nav.registre.sigrun.consumer.rs;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class SigrunStubConsumer {

    @Autowired
    private RestTemplate restTemplate;

    private String sigrunBaseUrl;

    public SigrunStubConsumer(@Value("${sigrunstub.url}") String sigrunServerUrl) {
        this.sigrunBaseUrl = sigrunServerUrl;
    }

    @Timed(value = "testnorge-sigrun.resource.latency", extraTags = { "operation", "sigrun-skd-stub" })
    public List<String> hentEksisterendePersonidentifikatorer() {
        String hentFnrUrl = sigrunBaseUrl + "testdata/hentPersonidentifikatorer";
        ResponseEntity<List> response = restTemplate.getForEntity(hentFnrUrl, List.class);

        List<String> identer = new ArrayList<>();

        if (response.getBody() != null) {
            identer.addAll(response.getBody());
        } else {
            log.error("SigrunStubConsumer.hentEksisterendePersonidentifikatorer: Kunne ikke hente response body fra sigrun-skd-stub: NullPointerException");
        }

        return identer;
    }

    @Timed(value = "testnorge-sigrun.resource.latency", extraTags = { "operation", "sigrun-skd-stub" })
    public ResponseEntity sendDataTilSigrunstub(List<Map<String, Object>> meldinger, String testdataEier) {
        String sendDataUrl = sigrunBaseUrl + "testdata/opprettBolk";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("testdataEier", testdataEier);
        HttpEntity entity = new HttpEntity(meldinger, headers);
        return restTemplate.postForEntity(sendDataUrl, entity, List.class);
    }
}
