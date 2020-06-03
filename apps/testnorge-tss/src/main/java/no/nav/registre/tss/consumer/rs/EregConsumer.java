package no.nav.registre.tss.consumer.rs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.List;

import no.nav.registre.tss.consumer.rs.request.EregMapperRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class EregConsumer {

    private final RestTemplate restTemplate;

    @Value("${ereg.url}")
    private String eregUrl;

    public boolean verifiserEnhet(String orgnr) {
        UriTemplate uriTemplate = new UriTemplate(eregUrl + "/v1/organisasjon/{orgnr}?inkluderHierarki=false&inkluderHistorikk=false");
        RequestEntity<List<EregMapperRequest>> request = new RequestEntity<>(HttpMethod.GET, uriTemplate.expand(orgnr));
        int retryCount = 0;
        while (retryCount != 5) {
            try {
                ResponseEntity<Object> response = restTemplate.exchange(request, Object.class);
                if (response.getStatusCode().is2xxSuccessful()) {
                    log.info("Fant ikke enhet {} i ereg", orgnr);
                }
                return response.getStatusCode().is2xxSuccessful();
            } catch (HttpClientErrorException e) {
                if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                    retryCount++;
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                        log.warn("Thread interupted");
                    }
                } else {
                    throw e;
                }
            }
        }
        return false;
    }
}
