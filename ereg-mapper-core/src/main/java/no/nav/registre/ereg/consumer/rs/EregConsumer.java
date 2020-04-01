package no.nav.registre.ereg.consumer.rs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
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

    //TODO: Check kun hvis det er oppretting
    public boolean checkExists(
            String orgnummer,
            String miljoe
    ) {
        RequestEntity getRequest = RequestEntity.get(new UriTemplate(eregApiUrl + "/v1/organisasjon/{orgnummer}").expand(miljoe, orgnummer)).build();
        try {
            ResponseEntity<Object> response = restTemplate.exchange(getRequest, Object.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return true;
            }
        } catch (HttpClientErrorException e) {
            return false;
        }
        return false;
    }
}
