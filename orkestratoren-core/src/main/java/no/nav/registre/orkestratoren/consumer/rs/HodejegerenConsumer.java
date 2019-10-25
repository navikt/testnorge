package no.nav.registre.orkestratoren.consumer.rs;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import no.rtv.namespacetps.TpsPersonDokumentType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.List;

@Component
@Slf4j
public class HodejegerenConsumer {

    private static final ParameterizedTypeReference<List<String>> RESPONSE_TYPE = new ParameterizedTypeReference<List<String>>() {
    };

    private RestTemplate restTemplate;
    private UriTemplate hentAlleIdenterUrl;
    private UriTemplate sendTilHodejegerenUrl;
    private UriTemplate hentIdenterSomIkkeErITpsUrl;
    private UriTemplate hentIdenterSomKollidererITps;

    public HodejegerenConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${testnorge-hodejegeren.rest.api.url}") String hodejegerenServerUrl) {
        this.restTemplate = restTemplateBuilder.build();
        this.hentAlleIdenterUrl = new UriTemplate(hodejegerenServerUrl + "/v1/alle-identer/{avspillergruppeId}");
        this.sendTilHodejegerenUrl = new UriTemplate(hodejegerenServerUrl + "/v1/historikk/skd/oppdaterDokument/{ident}");
        this.hentIdenterSomIkkeErITpsUrl = new UriTemplate(hodejegerenServerUrl + "/v1/identer-ikke-i-tps/{avspillergruppeId}?miljoe={miljoe}");
        this.hentIdenterSomKollidererITps = new UriTemplate(hodejegerenServerUrl + "/v1/identer-som-kolliderer/{avspillergruppeId}");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "hodejegeren" })
    public List<String> hentAlleIdenter(Long avspillergruppeId) {
        RequestEntity getRequest = RequestEntity.get(hentAlleIdenterUrl.expand(avspillergruppeId.toString())).build();
        return restTemplate.exchange(getRequest, RESPONSE_TYPE).getBody();
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "hodejegeren" })
    public List<String> sendTpsPersondokumentTilHodejegeren(TpsPersonDokumentType tpsPersonDokument, String personIdent) {
        RequestEntity postRequest = RequestEntity.post(sendTilHodejegerenUrl.expand(personIdent)).contentType(MediaType.APPLICATION_JSON).body(tpsPersonDokument);
        return restTemplate.exchange(postRequest, RESPONSE_TYPE).getBody();
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "hodejegeren" })
    public List<String> hentIdenterSomIkkeErITps(Long avspillergruppeId, String miljoe) {
        RequestEntity requestEntity = RequestEntity.get(hentIdenterSomIkkeErITpsUrl.expand(avspillergruppeId, miljoe)).build();
        return restTemplate.exchange(requestEntity, RESPONSE_TYPE).getBody();
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "hodejegeren" })
    public List<String> hentIdenterSomKollidererITps(Long avspillergruppeId) {
        RequestEntity requestEntity = RequestEntity.get(hentIdenterSomKollidererITps.expand(avspillergruppeId)).build();
        return restTemplate.exchange(requestEntity, RESPONSE_TYPE).getBody();
    }
}
