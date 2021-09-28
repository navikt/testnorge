package no.nav.registre.sam.consumer.rs;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.List;

import no.nav.registre.sam.domain.SamSaveInHodejegerenRequest;

@Component
@Slf4j
public class HodejegerenHistorikkConsumer {

    private static final ParameterizedTypeReference<List<String>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    private final RestTemplate restTemplate;

    private final UriTemplate hodejegerenSaveHistorikk;

    public HodejegerenHistorikkConsumer(RestTemplate restTemplate, @Value("${testnorge-hodejegeren.rest.api.url}") String hodejegerenServerUrl) {
        this.restTemplate = restTemplate;
        this.hodejegerenSaveHistorikk = new UriTemplate(hodejegerenServerUrl + "/v1/historikk");
    }

    @Timed(value = "sam.resource.latency", extraTags = { "operation", "hodejegeren" })
    public List<String> saveHistory(
            SamSaveInHodejegerenRequest request
    ) {
        var postRequest = RequestEntity.post(hodejegerenSaveHistorikk.expand()).body(request);
        return restTemplate.exchange(postRequest, RESPONSE_TYPE).getBody();
    }
}
