package no.nav.registre.orkestratoren.consumer.rs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.orkestratoren.consumer.rs.requests.GenereringsOrdreRequest;

@Component
@Slf4j
public class HodejegerenConsumer {

    private static final ParameterizedTypeReference<List<Long>> RESPONSE_TYPE = new ParameterizedTypeReference<List<Long>>() {
    };

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate url;

    public HodejegerenConsumer(@Value("${testnorge-hodejegeren.rest-api.url}") String hodejegerenServerUrl) {
        this.url = new UriTemplate(hodejegerenServerUrl + "/v1/syntetisering/generer");
    }

    public List<Long> startSyntetisering(GenereringsOrdreRequest genereringsOrdreRequest) {
        RequestEntity postRequest = RequestEntity.post(url.expand()).body(genereringsOrdreRequest);
        List<Long> ids = new ArrayList<>();
        try {
            ids.addAll(Objects.requireNonNull(restTemplate.exchange(postRequest, RESPONSE_TYPE).getBody(),
                    "Liste med ID-er fra Hodejegeren er null"));
        } catch (HttpStatusCodeException e) {
            try {
                JsonNode jsonNode = new ObjectMapper().readTree(e.getResponseBodyAsString()).get("ids");
                for (final JsonNode idNode : jsonNode) {
                    ids.add(idNode.asLong());
                }
            } catch (IOException ie) {
                log.warn("Kunne ikke id-er fra innholdet i exception fra Hodejegeren");
                ie.printStackTrace();
            }
        }
        return ids;
    }
}
