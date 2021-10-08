package no.nav.registre.tp.consumer.rs;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedList;
import java.util.List;

import no.nav.registre.tp.database.models.TYtelse;

@Slf4j
@Component
public class TpSyntConsumer {

    private static final ParameterizedTypeReference<List<TYtelse>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    private final RestTemplate restTemplate;
    private final String syntRestApiUrl;

    public TpSyntConsumer(
            RestTemplate restTemplate,
            @Value("${syntrest.rest.api.url}") String syntRestApiUrl
    ) {
        this.restTemplate = restTemplate;
        this.syntRestApiUrl = syntRestApiUrl + "/v1/generate/tp?numToGenerate={numToGenerate}";
    }

    @Timed(value = "tp.resource.latency", extraTags = { "operation", "synt" })
    public List<TYtelse> getSyntYtelser(
            int numToGenerate
    ) {
        var responseEntity = restTemplate.exchange(syntRestApiUrl, HttpMethod.GET, null, RESPONSE_TYPE, numToGenerate);
        List<TYtelse> ytelser = new LinkedList<>();
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            ytelser = responseEntity.getBody();
        }
        return ytelser;
    }
}
