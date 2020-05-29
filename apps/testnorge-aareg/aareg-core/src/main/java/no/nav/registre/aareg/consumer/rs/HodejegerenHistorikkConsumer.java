package no.nav.registre.aareg.consumer.rs;

import static no.nav.registre.aareg.domain.CommonKeys.RESPONSE_TYPE_LIST_STRING;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.List;

import no.nav.registre.aareg.AaregSaveInHodejegerenRequest;

@Component
@Slf4j
public class HodejegerenHistorikkConsumer {

    private final RestTemplate restTemplate;

    private final UriTemplate hodejegerenSaveHistorikk;

    public HodejegerenHistorikkConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${testnorge-hodejegeren.rest-api.url}") String hodejegerenServerUrl
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.hodejegerenSaveHistorikk = new UriTemplate(hodejegerenServerUrl + "/v1/historikk/");
    }

    @Timed(value = "aareg.resource.latency", extraTags = { "operation", "hodejegeren" })
    public List<String> saveHistory(AaregSaveInHodejegerenRequest request) {
        var postRequest = RequestEntity.post(hodejegerenSaveHistorikk.expand()).body(request);

        return restTemplate.exchange(postRequest, RESPONSE_TYPE_LIST_STRING).getBody();
    }
}
