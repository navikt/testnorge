package no.nav.registre.sigrun.consumer.rs;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.ArrayList;
import java.util.List;

import no.nav.registre.sigrun.SigrunSaveInHodejegerenRequest;
import no.nav.registre.sigrun.provider.rs.requests.SyntetiserPoppRequest;

@Component
@Slf4j
public class HodejegerenConsumer {

    private static final ParameterizedTypeReference<List<String>> RESPONSE_TYPE = new ParameterizedTypeReference<List<String>>() {
    };

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate hentLevendeIdenterUrl;

    private UriTemplate hodejegerenSaveHistorikk;

    public HodejegerenConsumer(@Value("${testnorge-hodejegeren.rest-api.url}") String hodejegerenServerUrl) {
        this.hentLevendeIdenterUrl = new UriTemplate(hodejegerenServerUrl +
                "/v1/levende-identer/{avspillergruppeId}?miljoe={miljoe}&antallPersoner={antallIdenter}");
        this.hodejegerenSaveHistorikk = new UriTemplate(hodejegerenServerUrl + "/v1/historikk");
    }

    @Timed(value = "testnorge-sigrun.resource.latency", extraTags = { "operation", "hodejegeren" })
    public List<String> finnLevendeIdenter(SyntetiserPoppRequest syntetiserPoppRequest) {
        RequestEntity getRequest = RequestEntity.get(hentLevendeIdenterUrl.expand(
                syntetiserPoppRequest.getAvspillergruppeId(),
                syntetiserPoppRequest.getMiljoe(),
                syntetiserPoppRequest.getAntallNyeIdenter()))
                .build();

        List<String> identer = new ArrayList<>();
        ResponseEntity<List<String>> response = restTemplate.exchange(getRequest, RESPONSE_TYPE);

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.warn("Fikk statuskode {} fra testnorge-hodejegeren", response.getStatusCode());
        }

        if (response.getBody() != null) {
            identer.addAll(response.getBody());
        } else {
            log.error("HodejegerenConsumer.finnLevendeIdenter: Kunne ikke hente response body fra Hodejegeren: NullPointerException");
        }

        return identer;
    }

    @Timed(value = "testnorge-sigrun.resource.latency", extraTags = { "operation", "hodejegeren" })
    public List<String> saveHistory(SigrunSaveInHodejegerenRequest request) {

        RequestEntity<SigrunSaveInHodejegerenRequest> postRequest = RequestEntity.post(hodejegerenSaveHistorikk.expand()).body(request);

        return restTemplate.exchange(postRequest, RESPONSE_TYPE).getBody();
    }
}
