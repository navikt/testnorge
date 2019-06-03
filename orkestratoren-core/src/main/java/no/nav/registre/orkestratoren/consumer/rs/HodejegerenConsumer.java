package no.nav.registre.orkestratoren.consumer.rs;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import io.micrometer.core.annotation.Timed;
import no.rtv.namespacetps.TpsPersonDokumentType;

@Component
public class HodejegerenConsumer {

    private static final ParameterizedTypeReference<List<String>> RESPONSE_TYPE = new ParameterizedTypeReference<List<String>>() {
    };

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate sendTilHodejegerenUrl;

    public HodejegerenConsumer(@Value("${testnorge-hodejegeren.rest-api.url}") String hodejegerenServerUrl) {
        this.sendTilHodejegerenUrl = new UriTemplate(hodejegerenServerUrl + "/v1/historikk/skd/oppdaterDokument/{ident}");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "hodejegeren" })
    public List<String> sendTpsPersondokumentTilHodejegeren(TpsPersonDokumentType tpsPersonDokument) {
        String ident = tpsPersonDokument.getPerson().getPersonIdent().get(0).getPersonIdent();
        RequestEntity postRequest = RequestEntity.post(sendTilHodejegerenUrl.expand(ident)).contentType(MediaType.APPLICATION_JSON).body(tpsPersonDokument);
        return restTemplate.exchange(postRequest, RESPONSE_TYPE).getBody();
    }
}
