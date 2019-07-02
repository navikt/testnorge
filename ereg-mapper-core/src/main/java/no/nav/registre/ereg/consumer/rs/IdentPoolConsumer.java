package no.nav.registre.ereg.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.List;

@Slf4j
@Component
public class IdentPoolConsumer {

    private static final ParameterizedTypeReference<List<String>> RESPONSE_TYPE = new ParameterizedTypeReference<List<String>>() {
    };
    private final UriTemplate nameServiceTemplate;
    private final RestTemplate restTemplate;

    public IdentPoolConsumer(@Value("${ident.pool.url}") String identPoolUrl, RestTemplate restTemplate) {
        nameServiceTemplate = new UriTemplate(identPoolUrl + "/v1/fiktive-navn/tilfeldig?antall={}");
        this.restTemplate = restTemplate;
    }

    public List<String> getFakeNames(int count) {
        ResponseEntity<List<String>> response = restTemplate.exchange(nameServiceTemplate.expand(count), HttpMethod.GET, new HttpEntity<>(), RESPONSE_TYPE);
        if (!response.getStatusCode().is2xxSuccessful()) {
            return null;
        }
        if (response.getBody() != null) {
            return response.getBody();
        }
        return null;
    }

}
