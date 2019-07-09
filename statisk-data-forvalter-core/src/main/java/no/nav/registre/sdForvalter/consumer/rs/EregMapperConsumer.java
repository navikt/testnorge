package no.nav.registre.sdForvalter.consumer.rs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.List;

import no.nav.registre.sdForvalter.database.model.EregModel;

@Component
public class EregMapperConsumer {

    private final RestTemplate restTemplate;
    private final String eregUrl;


    public EregMapperConsumer(RestTemplate restTemplate, @Value("${testnorge.ereg.mapper.rest.api.url}") String eregUrl) {
        this.restTemplate = restTemplate;
        this.eregUrl = eregUrl + "/v1";
    }

    public String uploadToEreg(List<EregModel> data, String env) {
        UriTemplate uriTemplate = new UriTemplate(eregUrl + "/orkestrering/opprett?lastOpp=true&miljoe={miljoe}");
        RequestEntity<List<EregModel>> requestEntity = new RequestEntity<>(data, HttpMethod.POST, uriTemplate.expand(env));
        ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);
        if (response.getBody() != null) {
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            }
        }
        return null;
    }

}
