package no.nav.testnav.apps.syntaaregservice.consumer;

import no.nav.testnav.apps.syntaaregservice.consumer.response.KodeverkResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

@Component
public class KodeverkConsumer {

    private static final String CALL_ID = "Orkestratoren";
    private static final String CONSUMER_ID = "Orkestratoren";

    private final RestTemplate restTemplate;

    private final UriTemplate getYrkeskoderUrl;

    public KodeverkConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${consumers.kodeverk.url}") String kodeverkUrl
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.getYrkeskoderUrl = new UriTemplate(kodeverkUrl + "/api/v1/kodeverk/Yrker/koder");
    }

    public KodeverkResponse getYrkeskoder() {
        var getRequest = RequestEntity.get(getYrkeskoderUrl.expand())
                .header("Nav-Call-Id", CALL_ID)
                .header("Nav-Consumer-Id", CONSUMER_ID)
                .build();
        return restTemplate.exchange(getRequest, KodeverkResponse.class).getBody();
    }
}
