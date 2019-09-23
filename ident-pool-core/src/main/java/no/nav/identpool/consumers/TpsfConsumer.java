package no.nav.identpool.consumers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.io.IOException;
import java.util.List;

@Component
public class TpsfConsumer {

    private static final String environment = "q2";

    private RestTemplate restTemplate;
    private UriTemplate url;

    public TpsfConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${tps-forvalteren.rest-api.url}") String serverUrl
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.url = new UriTemplate(serverUrl + "/v1/serviceroutine/FS03-FDLISTER-DISKNAVN-M?aksjonsKode=A2&antallFnr={numberOfIdents}&environment={environment}&nFnr={idents}");
    }

    public JsonNode getStatusFromTps(List<String> idents) throws IOException {
        String identsAsString = String.join(",", idents);
        RequestEntity getRequest = RequestEntity.get(url.expand(idents.size(), environment, identsAsString)).build();
        ResponseEntity<String> response = restTemplate.exchange(getRequest, String.class);
        return new ObjectMapper().readTree(response.getBody()).findValue("data1");
    }
}
