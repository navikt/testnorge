package no.nav.registre.orkestratoren.consumer.rs;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;

import no.nav.registre.testnorge.domain.dto.namespacetps.TpsPersonDokumentType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class HodejegerenHistorikkConsumer {

    private static final ParameterizedTypeReference<List<String>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    private RestTemplate restTemplate;
    private UriTemplate sendTilHodejegerenUrl;
    private UriTemplate oppdaterHodejegerenCacheUrl;

    public HodejegerenHistorikkConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${testnorge-hodejegeren.rest.api.url}") String hodejegerenServerUrl
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.sendTilHodejegerenUrl = new UriTemplate(hodejegerenServerUrl + "/v1/historikk/skd/oppdaterDokument/{ident}");
        this.oppdaterHodejegerenCacheUrl = new UriTemplate(hodejegerenServerUrl + "/v1/cache/oppdaterGruppe/{avspillergruppeId}");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "hodejegeren" })
    public List<String> sendTpsPersondokumentTilHodejegeren(
            TpsPersonDokumentType tpsPersonDokument,
            String personIdent
    ) {
        var postRequest = RequestEntity.post(sendTilHodejegerenUrl.expand(personIdent)).contentType(MediaType.APPLICATION_JSON).body(tpsPersonDokument);
        List<String> response = new ArrayList<>();
        try {
            response = restTemplate.exchange(postRequest, RESPONSE_TYPE).getBody();
        } catch (HttpStatusCodeException e) {
            log.error("Kunne ikke sende tps-persondokument for ident {} til hodejegeren. ", personIdent, e);
        }
        return response;
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "hodejegeren" })
    @Async
    public void oppdaterHodejegerenCache(
            Long avspillergruppeId
    ) {
        var getRequest = RequestEntity.get(oppdaterHodejegerenCacheUrl.expand(avspillergruppeId)).build();
        restTemplate.exchange(getRequest, String.class);
    }
}
