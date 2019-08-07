package no.nav.registre.sigrun.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class PoppSyntetisererenConsumer {

    private static final ParameterizedTypeReference<List<Map<String, Object>>> RESPONSE_TYPE = new ParameterizedTypeReference<List<Map<String, Object>>>() {
    };

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate url;

    public PoppSyntetisererenConsumer(@Value("${syntrest.rest.api.url}") String syntrestServerUrl) {
        this.url = new UriTemplate(syntrestServerUrl + "/v1/generate/popp");
    }

    public List<Map<String, Object>> hentPoppMeldingerFromSyntRest(List<String> fnrs) {
        RequestEntity postRequest = RequestEntity.post(url.expand()).body(fnrs);

        return restTemplate.exchange(postRequest, RESPONSE_TYPE).getBody();
    }
}
