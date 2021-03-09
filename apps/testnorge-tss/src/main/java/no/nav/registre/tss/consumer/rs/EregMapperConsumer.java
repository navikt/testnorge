package no.nav.registre.tss.consumer.rs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.tss.consumer.rs.request.EregMapperRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class EregMapperConsumer {

    private static final ParameterizedTypeReference<List<String>> ORGNR_RESPONSE = new ParameterizedTypeReference<>() {
    };
    private final RestTemplate restTemplate;
    @Value("${miljoe}")
    String miljoe;
    @Value("${testnorge.ereg.mapper.url}")
    private String eregMapperUrl;

    public boolean opprett(List<EregMapperRequest> data) {
        log.info("Prøver å opprette følgende enheter i EREG: {}", data.stream().map(EregMapperRequest::getOrgnr).collect(Collectors.toList()));
        UriTemplate uriTemplate = new UriTemplate(eregMapperUrl + "/v1/orkestrering/opprett?lastOpp=true&miljoe={miljoe}");
        RequestEntity<List<EregMapperRequest>> request = new RequestEntity<>(data, HttpMethod.POST, uriTemplate.expand(miljoe));
        ResponseEntity<String> response = restTemplate.exchange(request, String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Oppretting av EREG enheter er sendt til ereg i {}", miljoe);
        }
        return response.getStatusCode().is2xxSuccessful();
    }

    public List<String> hentNyttOrgnr(int antall) {
        UriTemplate uriTemplate = new UriTemplate(eregMapperUrl + "/v1/orgnr?antall={antall}");
        RequestEntity<?> request = new RequestEntity<>(HttpMethod.GET, uriTemplate.expand(antall));
        ResponseEntity<List<String>> response = restTemplate.exchange(request, ORGNR_RESPONSE);
        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Bruker orgnr: {}", response.getBody());
        }
        return response.getBody();
    }
}
