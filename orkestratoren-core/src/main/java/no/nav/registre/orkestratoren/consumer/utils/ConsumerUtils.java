package no.nav.registre.orkestratoren.consumer.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserArenaRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConsumerUtils {

    private final RestTemplate restTemplate;

    public void sendRequest(
            UriTemplate url,
            SyntetiserArenaRequest request,
            String info
    ) {
        var postRequest = RequestEntity.post(url.expand())
                .contentType(MediaType.APPLICATION_JSON)
                .body(request);

        try {
            restTemplate.exchange(postRequest, Object.class).getBody();
        } catch (HttpStatusCodeException e) {
            log.error("Feil under syntetisering av '{}'", info, e);
        }
    }
}
