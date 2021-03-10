package no.nav.registre.sigrun.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.List;

import no.nav.registre.sigrun.PoppSyntetisererenResponse;

@Component
@Slf4j
public class PoppSyntetisererenConsumer {

    private static final ParameterizedTypeReference<List<PoppSyntetisererenResponse>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate url;

    public PoppSyntetisererenConsumer(@Value("${syntrest.rest.api.url}") String syntrestServerUrl) {
        this.url = new UriTemplate(syntrestServerUrl + "/v1/generate/popp");
    }

    public List<PoppSyntetisererenResponse> hentPoppMeldingerFromSyntRest(List<String> fnrs) {
        var postRequest = RequestEntity.post(url.expand())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(fnrs);
        return restTemplate.exchange(postRequest, RESPONSE_TYPE).getBody();
    }
}
