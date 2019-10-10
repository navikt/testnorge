package no.nav.registre.tss.consumer.rs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.List;
import java.util.Map;

import no.nav.registre.tss.consumer.rs.response.TssSyntMessage;
import no.nav.registre.tss.domain.Samhandler;

@Component
public class TssSyntetisererenConsumer {

    private final ParameterizedTypeReference<Map<String, List<TssSyntMessage>>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    private RestTemplate restTemplate;
    private UriTemplate url;

    public TssSyntetisererenConsumer(RestTemplateBuilder restTemplateBuilder, @Value("${synthdata-tss-api-url}") String synthdataTssUrl) {
        this.restTemplate = restTemplateBuilder.build();
        this.url = new UriTemplate(synthdataTssUrl + "/v1/tss");
    }

    public Map<String, List<TssSyntMessage>> hentSyntetiskeTssRutiner(List<Samhandler> samhandlere) {
        RequestEntity postRequest = RequestEntity.post(url.expand()).body(samhandlere);
        return restTemplate.exchange(postRequest, RESPONSE_TYPE).getBody();
    }
}
