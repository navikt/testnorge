package no.nav.identpool.consumers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TpsfConsumer {

    private static final int MAX_IDENTS = 80;
    private static final String ENVIRONMENT = "q2";
    private static final String TPS_STATUS = "/api/v1/testdata/tpsStatus?identer={identer}&includeProd={includeProd}";

    private final RestTemplate restTemplate;
    private final UriTemplate url;
    private final String serverUrl;

    public TpsfConsumer(RestTemplateBuilder restTemplateBuilder,
                        @Value("${tps-forvalteren.rest.api.url}") String serverUrl) {

        this.restTemplate = restTemplateBuilder.build();
        this.serverUrl = serverUrl;
        this.url = new UriTemplate(serverUrl + "/api/v1/serviceroutine/FS03-FDLISTER-DISKNAVN-M?aksjonsKode=A2&antallFnr={numberOfIdents}&environment={environment}&nFnr={idents}");
    }

    @Retryable(exclude = HttpClientErrorException.NotFound.class)
    public JsonNode getProdStatusFromTps(List<String> idents) throws IOException {

        String identsAsString = String.join(",", idents);
        RequestEntity<Void> getRequest = RequestEntity.get(url.expand(idents.size(), ENVIRONMENT, identsAsString)).build();
        ResponseEntity<String> response = restTemplate.exchange(getRequest, String.class);
        return new ObjectMapper().readTree(response.getBody()).findValue("data1");
    }

    @Retryable(exclude = HttpClientErrorException.NotFound.class)
    public TpsfStatusResponse getStatusFromTpsf(Collection<String> idents, Boolean includeProd) {

        ResponseEntity<TpsfStatusResponse> response = restTemplate.exchange(RequestEntity.get(new UriTemplate(serverUrl + TPS_STATUS)
                        .expand(String.join(",", idents), includeProd))
                .build(), TpsfStatusResponse.class);
        return response.hasBody() ? response.getBody() : new TpsfStatusResponse();
    }

    public TpsfStatusResponse getStatusFromTpsf(Set<String> idents, Boolean includeProd) {

        List<List<String>> identChunks = Lists.partition(new ArrayList<>(idents), MAX_IDENTS);
        return TpsfStatusResponse.builder()
                .statusPaaIdenter(identChunks.stream()
                        .map(chunk -> getStatusFromTpsf(chunk, includeProd))
                        .flatMap(response -> response.getStatusPaaIdenter().stream())
                        .collect(Collectors.toList()))
                .build();
    }
}
