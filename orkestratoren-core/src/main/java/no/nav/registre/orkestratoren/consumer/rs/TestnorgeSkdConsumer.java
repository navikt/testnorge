package no.nav.registre.orkestratoren.consumer.rs;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
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

import no.nav.registre.orkestratoren.consumer.rs.response.SkdMeldingerTilTpsRespons;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserSkdmeldingerRequest;

@Component
@Slf4j
public class TestnorgeSkdConsumer {

    private static final ParameterizedTypeReference<SkdMeldingerTilTpsRespons> RESPONSE_TYPE_START_SYNT = new ParameterizedTypeReference<SkdMeldingerTilTpsRespons>() {
    };
    private static final ParameterizedTypeReference<List<Long>> RESPONSE_TYPE_DELETE = new ParameterizedTypeReference<List<Long>>() {
    };

    private RestTemplate restTemplate;
    private UriTemplate startSyntetiseringUrl;
    private UriTemplate slettIdenterUrl;

    public TestnorgeSkdConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${testnorge-skd.rest-api.url}") String skdServerUrl) {
        this.restTemplate = restTemplateBuilder.build();
        this.startSyntetiseringUrl = new UriTemplate(skdServerUrl + "/v1/syntetisering/generer");
        this.slettIdenterUrl = new UriTemplate(skdServerUrl + "/v1/ident/{avspillergruppeId}?miljoer={miljoer}");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "skd" })
    public ResponseEntity startSyntetisering(SyntetiserSkdmeldingerRequest syntetiserSkdmeldingerRequest) {
        RequestEntity postRequest = RequestEntity.post(startSyntetiseringUrl.expand()).contentType(MediaType.APPLICATION_JSON).body(syntetiserSkdmeldingerRequest);
        return restTemplate.exchange(postRequest, RESPONSE_TYPE_START_SYNT);
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "skd" })
    public List<Long> slettIdenterFraAvspillerguppe(Long avspillergruppeId, List<String> miljoer, List<String> identer) {
        String miljoerSomString = String.join(",", miljoer);
        RequestEntity deleteRequest = RequestEntity.method(HttpMethod.DELETE, slettIdenterUrl.expand(avspillergruppeId, miljoerSomString)).contentType(MediaType.APPLICATION_JSON).body(identer);
        return restTemplate.exchange(deleteRequest, RESPONSE_TYPE_DELETE).getBody();
    }
}
