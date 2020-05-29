package no.nav.registre.orkestratoren.consumer.rs;

import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.List;

import no.nav.registre.orkestratoren.consumer.rs.response.SletteSkattegrunnlagResponse;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserPoppRequest;

@Component
public class TestnorgeSigrunConsumer {

    private static final ParameterizedTypeReference<List<Integer>> RESPONSE_TYPE_START_SYNT = new ParameterizedTypeReference<>() {
    };
    private static final ParameterizedTypeReference<SletteSkattegrunnlagResponse> RESPONSE_TYPE_DELETE = new ParameterizedTypeReference<>() {
    };

    private RestTemplate restTemplate;
    private UriTemplate startSyntetiseringUrl;
    private UriTemplate slettIdenterUrl;

    public TestnorgeSigrunConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${testnorge-sigrun.rest.api.url}") String sigrunServerUrl
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.startSyntetiseringUrl = new UriTemplate(sigrunServerUrl + "/v1/syntetisering/generer");
        this.slettIdenterUrl = new UriTemplate(sigrunServerUrl + "/v1/ident?miljoe={miljoe}");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "sigrun" })
    public ResponseEntity startSyntetisering(
            SyntetiserPoppRequest syntetiserPoppRequest,
            String testdataEier
    ) {
        var postRequest = RequestEntity.post(startSyntetiseringUrl.expand()).header("testdataEier", testdataEier).contentType(MediaType.APPLICATION_JSON).body(syntetiserPoppRequest);
        return restTemplate.exchange(postRequest, RESPONSE_TYPE_START_SYNT);
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "sigrun" })
    public SletteSkattegrunnlagResponse slettIdenterFraSigrun(
            String testdataEier,
            String miljoe,
            List<String> identer
    ) {
        var deleteRequest = RequestEntity.method(HttpMethod.DELETE, slettIdenterUrl.expand(miljoe))
                .contentType(MediaType.APPLICATION_JSON)
                .header("testdataEier", testdataEier)
                .body(identer);
        return restTemplate.exchange(deleteRequest, RESPONSE_TYPE_DELETE).getBody();
    }
}
