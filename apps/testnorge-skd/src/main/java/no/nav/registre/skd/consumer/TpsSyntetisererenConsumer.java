package no.nav.registre.skd.consumer;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.List;

import no.nav.registre.skd.skdmelding.RsMeldingstype;

@Component
@Slf4j
public class TpsSyntetisererenConsumer {

    private static final ParameterizedTypeReference<List<RsMeldingstype>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    private final String serverUrl;

    private final RestTemplate restTemplate;

    public TpsSyntetisererenConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${syntrest.rest.api.url}") String syntrestServerUrl
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.serverUrl = syntrestServerUrl;
    }

    @Timed(value = "skd.resource.latency", extraTags = { "operation", "tps-syntetisereren" })
    public List<RsMeldingstype> getSyntetiserteSkdmeldinger(
            String endringskode,
            Integer antallMeldinger
    ) {
        var uriTemplate = new UriTemplate(serverUrl + "/v1/generate/tps/{endringskode}?numToGenerate={antallMeldinger}");
        var getRequest = RequestEntity.get(uriTemplate.expand(endringskode, antallMeldinger)).build();
        var response = restTemplate.exchange(getRequest, RESPONSE_TYPE);

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.warn("Fikk statuskode {} fra TPS-Syntetisereren", response.getStatusCode());
        }

        var responseBody = response.getBody();

        if (responseBody != null && responseBody.size() != antallMeldinger) {
            log.warn("Feil antall meldinger mottatt fra TPS-Syntetisereren. Forventet {}, men mottok {} meldinger.", antallMeldinger, responseBody.size());
        }

        return responseBody;
    }
}