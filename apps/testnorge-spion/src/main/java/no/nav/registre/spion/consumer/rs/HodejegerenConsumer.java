package no.nav.registre.spion.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.spion.consumer.rs.response.HodejegerenResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;


@Slf4j
@Component
public class HodejegerenConsumer {

    private final RestTemplate restTemplate;
    private final String hodejegerenUrl;

    public HodejegerenConsumer(
            RestTemplate restTemplate,
            @Value("${testnorge.rest-api.hodejegeren}") String serverUrl
    ) {
        this.restTemplate = restTemplate;
        this.hodejegerenUrl = serverUrl;
    }

    public HodejegerenResponse hentPersondataTilIdent(String ident, String miljoe) {
        UriTemplate uriTemplate = new UriTemplate(hodejegerenUrl
                + "/v1/persondata?ident={ident}&miljoe={miljoe}");
        var getRequest = RequestEntity.get(uriTemplate.expand(ident, miljoe)).build();

        try{
            return restTemplate.exchange(getRequest, HodejegerenResponse.class).getBody();
        }catch(HttpStatusCodeException e){
            log.error("Kunne ikke hente persondata for ident fra hodejegeren.");
            throw e;
        }
    }
}