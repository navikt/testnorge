package no.nav.registre.udistub.core.service;

import static java.lang.String.format;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.udistub.core.exception.NotFoundException;
import no.nav.registre.udistub.core.service.tpsf.TpsfPerson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
public class TpsfService {
    private static final String TPSF_HENT_PERSON_URL = "/personerdata";

    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;

    @Value("${tpsf.base.url}")
    private String tpsfBaseUrl;

    public TpsfService(RestTemplate restTemplate,
            ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
    }

    public TpsfPerson hentPersonWithIdent(String ident, String consumerId) {
        String uri = UriComponentsBuilder.fromHttpUrl(format("%s%s", tpsfBaseUrl, TPSF_HENT_PERSON_URL))
                .queryParam("identer", ident)
                .build().toUriString();
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, consumerId);
        try {
            ResponseEntity<Object> response = restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(headers), Object.class);
            TpsfPerson[] personer = objectMapper.convertValue(response.getBody(), TpsfPerson[].class);
            if (personer != null && personer.length > 0) {
                return personer[0];
            } else {
                throw new NotFoundException(format("Ingen personer med ident:%s ble funnet i tpsf", ident));
            }
        } catch (Exception e) {
            log.error("Tps-forvalteren sitt kall feilet mot url <{}> grunnet {}", uri, e.getMessage());
        }
        return null;
    }
}