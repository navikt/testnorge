package no.nav.registre.aareg.consumer.rs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import no.nav.registre.aareg.consumer.rs.response.KodeverkResponse;

@Component
public class KodeverkConsumer {

    private static final String CALL_ID = "Orkestratoren";
    private static final String CONSUMER_ID = "Orkestratoren";

    private final RestTemplate restTemplate;

    private final UriTemplate getYrkeskoderUrl;

    public KodeverkConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${kodeverk.rest-api.url}") String hodejegerenServerUrl
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.getYrkeskoderUrl = new UriTemplate(hodejegerenServerUrl + "/v1/kodeverk/Yrker/koder");
    }

    public KodeverkResponse getYrkeskoder() {
        var getRequest = RequestEntity.get(getYrkeskoderUrl.expand())
                .header("Nav-Call-Id", CALL_ID)
                .header("Nav-Consumer-Id", CONSUMER_ID)
                .build();
        return restTemplate.exchange(getRequest, KodeverkResponse.class).getBody();
    }
}
