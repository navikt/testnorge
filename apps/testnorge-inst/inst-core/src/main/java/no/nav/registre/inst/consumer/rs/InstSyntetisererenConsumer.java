package no.nav.registre.inst.consumer.rs;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.List;

import no.nav.registre.inst.Institusjonsopphold;
import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;

@Component
@Slf4j
@DependencyOn"syntrest")
public class InstSyntetisererenConsumer {

    private static final ParameterizedTypeReference<List<Institusjonsopphold>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate url;

    public InstSyntetisererenConsumer(@Value("${syntrest.rest.api.url}") String syntrestServerUrl) {
        this.url = new UriTemplate(syntrestServerUrl + "/v1/generate/inst?numToGenerate={numToGenerate}");
    }

    @Timed(value = "inst.resource.latency", extraTags = { "operation", "inst-syntetisereren" })
    public List<Institusjonsopphold> hentInstMeldingerFromSyntRest(int numToGenerate) {
        var getRequest = RequestEntity.get(url.expand(numToGenerate)).build();
        return restTemplate.exchange(getRequest, RESPONSE_TYPE).getBody();
    }
}
