package no.nav.registre.ereg.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class EregConsumer {

    private final RestTemplate restTemplate;

    @Value("${EREG_API_URL}")
    private String eregApiUrl;

    public boolean checkExists(String orgnummer) {
        UriTemplate uriTemplate = new UriTemplate(eregApiUrl + "/v1/organisasjon/{orgnummer}");
        try {
            ResponseEntity<Object> response = restTemplate.exchange(uriTemplate.expand(orgnummer), HttpMethod.GET, null, Object.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return true;
            }
        } catch (HttpClientErrorException e) {
            return false;
        }
        return false;
    }

}
