package no.nav.identpool.consumers;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class TpsfConsumer {

    private static final String ENVIRONMENT = "q2";
    private static final String TPS_STATUS = "/api/v1/testdata/tpsStatus?identer={identer}&includeProd={includeProd}";

    private RestTemplate restTemplate;
    private UriTemplate url;
    private String serverUrl;

    public TpsfConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${tps-forvalteren.rest.api.url}") String serverUrl
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.serverUrl = serverUrl;
        this.url = new UriTemplate(serverUrl + "/v1/serviceroutine/FS03-FDLISTER-DISKNAVN-M?aksjonsKode=A2&antallFnr={numberOfIdents}&environment={environment}&nFnr={idents}");
    }

    public JsonNode getProdStatusFromTps(List<String> idents) throws IOException {
        String identsAsString = String.join(",", idents);
        RequestEntity<Void> getRequest = RequestEntity.get(url.expand(idents.size(), ENVIRONMENT, identsAsString)).build();
        ResponseEntity<String> response = restTemplate.exchange(getRequest, String.class);
        return new ObjectMapper().readTree(response.getBody()).findValue("data1");
    }

    public TpsfStatusResponse getStatusFromTpsf(Set<String> idents, Boolean includeProd) {

        ResponseEntity<TpsfStatusResponse> response = restTemplate.exchange(RequestEntity.get(new UriTemplate(serverUrl + TPS_STATUS)
                .expand(String.join(",", idents), includeProd))
                .build(), TpsfStatusResponse.class);
        return response.hasBody() ? response.getBody() : new TpsfStatusResponse();
    }
}
