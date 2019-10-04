package no.nav.registre.arena.core.consumer.rs;

import io.micrometer.core.annotation.Timed;
import no.nav.registre.arena.domain.aap.AAPMelding;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.List;

@Component
public class AAPNyRettighetSyntetisererenConsumer {
    private static final ParameterizedTypeReference<List<AAPMelding>> RESPONS_TYPE = new ParameterizedTypeReference<List<AAPMelding>>() {
    };

    private final RestTemplate restTemplate;
    private UriTemplate uriTemplate;

    public AAPNyRettighetSyntetisererenConsumer(RestTemplate restTemplate,
                                                @Value("${synt.rest-api.url}") String syntetisererenUrl) {
        this.restTemplate = restTemplate;
        this.uriTemplate = new UriTemplate(syntetisererenUrl + "/v1/generate/arena/aap/nyRettighet?numToGenerate={numToGenerate}");
    }

    @Timed(value = "aap-nyRettighet.resource.latency", extraTags = {"operation", "aap-nyRettighet-syntetisereren"})
    public List<AAPMelding> hentAAPMeldingerFraSyntRest(int numToGenerate) {
        RequestEntity requestEntity = RequestEntity.get(uriTemplate.expand(numToGenerate)).build();
        return restTemplate.exchange(requestEntity, RESPONS_TYPE).getBody();
    }
}
