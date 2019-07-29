package no.nav.registre.orkestratoren.consumer.rs;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.List;

import no.nav.registre.orkestratoren.consumer.rs.requests.GenereringsOrdreRequest;
import no.nav.registre.orkestratoren.consumer.rs.response.SkdMeldingerTilTpsRespons;

@Component
@Slf4j
public class TestnorgeSkdConsumer {

    private static final ParameterizedTypeReference<SkdMeldingerTilTpsRespons> RESPONSE_TYPE_START_SYNT = new ParameterizedTypeReference<SkdMeldingerTilTpsRespons>() {
    };
    private static final ParameterizedTypeReference<List<Long>> RESPONSE_TYPE_DELETE = new ParameterizedTypeReference<List<Long>>() {
    };

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate startSyntetiseringUrl;
    private UriTemplate slettIdenterUrl;

    public TestnorgeSkdConsumer(@Value("${testnorge-skd.rest-api.url}") String skdServerUrl) {
        this.startSyntetiseringUrl = new UriTemplate(skdServerUrl + "/v1/syntetisering/generer");
        this.slettIdenterUrl = new UriTemplate(skdServerUrl + "/v1/ident/{avspillergruppeId}");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "skd" })
    public ResponseEntity startSyntetisering(GenereringsOrdreRequest genereringsOrdreRequest) {
        RequestEntity postRequest = RequestEntity.post(startSyntetiseringUrl.expand()).contentType(MediaType.APPLICATION_JSON).body(genereringsOrdreRequest);
        return restTemplate.exchange(postRequest, RESPONSE_TYPE_START_SYNT);
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "skd" })
    public List<Long> slettIdenterFraAvspillerguppe(Long avspillergruppeId, List<String> identer) {
        RequestEntity deleteRequest = RequestEntity.method(HttpMethod.DELETE, slettIdenterUrl.expand(avspillergruppeId)).contentType(MediaType.APPLICATION_JSON).body(identer);
        return restTemplate.exchange(deleteRequest, RESPONSE_TYPE_DELETE).getBody();
    }
}
