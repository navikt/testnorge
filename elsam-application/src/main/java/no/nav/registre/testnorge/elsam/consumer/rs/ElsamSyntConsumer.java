package no.nav.registre.testnorge.elsam.consumer.rs;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.elsam.consumer.rs.response.synt.ElsamSyntResponse;

@Slf4j
@Component
public class ElsamSyntConsumer {

    private static final ParameterizedTypeReference<Map<String, ElsamSyntResponse>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    private final RestTemplate restTemplate;

    private UriTemplate genererSykemeldingerUrl;

    public ElsamSyntConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${syntrest.rest.api.url}") String syntUrl) {
        this.genererSykemeldingerUrl = new UriTemplate(syntUrl + "/v1/generate_sykmeldings_history_json");
        this.restTemplate = restTemplateBuilder.build();
    }

    @Timed(value = "elsam.resource.latency", extraTags = { "operation", "elsam-syntetisering" })
    public Map<String, ElsamSyntResponse> syntetiserSykemeldinger(List<Map<String, String>> sykemeldingRequest) {
        log.info("Oppretter syntentisert sykemelding for {}", sykemeldingRequest.stream().map(value -> String.join(", ", value.keySet())).collect(Collectors.joining(", ")));

        RequestEntity postRequest = RequestEntity.post(genererSykemeldingerUrl.expand()).body(sykemeldingRequest);
        ResponseEntity<Map<String, ElsamSyntResponse>> response = restTemplate.exchange(postRequest, RESPONSE_TYPE);

        return response.getBody();
    }
}
