package no.nav.registre.ereg.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.ereg.consumer.rs.response.NameResponse;

@Slf4j
@Component
public class IdentPoolConsumer {

    private static final ParameterizedTypeReference<List<NameResponse>> RESPONSE_TYPE = new ParameterizedTypeReference<List<NameResponse>>() {
    };
    private final UriTemplate nameServiceTemplate;
    private final RestTemplate restTemplate;

    public IdentPoolConsumer(@Value("${ident.pool.url}") String identPoolUrl, RestTemplate restTemplate) {
        nameServiceTemplate = new UriTemplate(identPoolUrl + "/v1/fiktive-navn/tilfeldig?antall={antall}");
        this.restTemplate = restTemplate;
    }

    public List<String> getFakeNames(Integer count) {
        try {
            ResponseEntity<List<NameResponse>> response = restTemplate.exchange(nameServiceTemplate.expand(count), HttpMethod.GET, null, RESPONSE_TYPE);
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new RuntimeException("Klarte ikke hente ut fake names fra IdentPool (Respons statuskode: " + response.getStatusCodeValue() + ")");
            }
            return response.getBody().stream().map(NameResponse::toString).collect(Collectors.toList());
        } catch (HttpClientErrorException e) {
            log.warn("Request feiler med url: {}", nameServiceTemplate.expand(count));
            log.error(e.getLocalizedMessage(), e);
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, e.getStatusText());
        }
    }

}
