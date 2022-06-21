package no.nav.registre.inst.consumer.rs;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.inst.domain.InstSaveInHodejegerenRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.List;

@Component
@Slf4j
public class HodejegerenHistorikkConsumer {

    private static final ParameterizedTypeReference<List<String>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate hodejegerenSaveHistorikk;

    public HodejegerenHistorikkConsumer(@Value("${testnorge-hodejegeren.rest-api.url}") String hodejegerenServerUrl) {
        this.hodejegerenSaveHistorikk = new UriTemplate(hodejegerenServerUrl + "/v1/historikk/");
    }

    @Timed(value = "inst.resource.latency", extraTags = { "operation", "hodejegeren" })
    public List<String> saveHistory(InstSaveInHodejegerenRequest request) {
        var postRequest = RequestEntity.post(hodejegerenSaveHistorikk.expand()).body(request);
        return restTemplate.exchange(postRequest, RESPONSE_TYPE).getBody();
    }
}
