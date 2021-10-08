package no.nav.registre.tp.consumer.rs;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

import no.nav.registre.tp.domain.TpSaveInHodejegerenRequest;

@Component
@Slf4j
public class HodejegerenHistorikkConsumer {

    private static final ParameterizedTypeReference<List<String>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    private final RestTemplate restTemplate;
    private final String hodejegerenSaveHistorikk;

    public HodejegerenHistorikkConsumer(
            RestTemplate restTemplate,
            @Value("${testnorge-hodejegeren.rest-api.url}") String hodejegerenUrl
    ) {
        this.restTemplate = restTemplate;
        this.hodejegerenSaveHistorikk = hodejegerenUrl + "/v1/historikk/";
    }

    @Timed(value = "tp.resource.latency", extraTags = { "operation", "hodejegeren" })
    public List<String> saveHistory(
            TpSaveInHodejegerenRequest request
    ) {
        var postRequest = RequestEntity.post(URI.create(hodejegerenSaveHistorikk)).body(request);
        return restTemplate.exchange(postRequest, RESPONSE_TYPE).getBody();
    }

}
