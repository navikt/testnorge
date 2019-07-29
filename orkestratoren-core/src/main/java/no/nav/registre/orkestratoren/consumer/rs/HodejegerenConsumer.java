package no.nav.registre.orkestratoren.consumer.rs;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import no.rtv.namespacetps.TpsPersonDokumentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate hentAlleIdenterUrl;
    private UriTemplate sendTilHodejegerenUrl;

    public HodejegerenConsumer(@Value("${testnorge-hodejegeren.rest-api.url}") String hodejegerenServerUrl) {
        this.hentAlleIdenterUrl = new UriTemplate(hodejegerenServerUrl + "/v1/alle-identer/{avspillergruppeId}");
        this.sendTilHodejegerenUrl = new UriTemplate(hodejegerenServerUrl + "/v1/historikk/skd/oppdaterDokument/{ident}");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "hodejegeren" })
    public List<String> finnAlleIdenter(Long avspillergruppeId) {
        RequestEntity getRequest = RequestEntity.get(hentAlleIdenterUrl.expand(avspillergruppeId.toString())).build();
        return restTemplate.exchange(getRequest, RESPONSE_TYPE).getBody();
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "hodejegeren" })
    public List<String> sendTpsPersondokumentTilHodejegeren(TpsPersonDokumentType tpsPersonDokument, String personIdent) {
        RequestEntity postRequest = RequestEntity.post(sendTilHodejegerenUrl.expand(personIdent)).contentType(MediaType.APPLICATION_JSON).body(tpsPersonDokument);
        return restTemplate.exchange(postRequest, RESPONSE_TYPE).getBody();
    }
}
