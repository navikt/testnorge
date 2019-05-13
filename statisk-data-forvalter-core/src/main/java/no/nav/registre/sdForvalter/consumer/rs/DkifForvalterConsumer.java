package no.nav.registre.sdForvalter.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.sdForvalter.consumer.rs.request.DkifRequest;
import no.nav.registre.sdForvalter.database.model.KrrModel;

@Slf4j
@Component
public class DkifForvalterConsumer {

    private static final ParameterizedTypeReference<Set<String>> RESPONSE_TYPE_SET = new ParameterizedTypeReference<Set<String>>() {
    };
    private final RestTemplate restTemplate;
    private final String dkifForvalterUrl;

    public DkifForvalterConsumer(RestTemplate restTemplate, @Value("${dkif.forvalter.rest.api.url}") String dkifForvalterUrl) {
        this.restTemplate = restTemplate;
        this.dkifForvalterUrl = dkifForvalterUrl + "/v1";
    }

    public Set<String> send(List<KrrModel> data, String miljoe) {

        UriTemplate uriTemplate = new UriTemplate(dkifForvalterUrl + "/lagre/" + miljoe);

        List<DkifRequest> dkifRequests = data.parallelStream().map(DkifRequest::new).collect(Collectors.toList());

        RequestEntity<List<DkifRequest>> listRequestEntity = new RequestEntity<>(dkifRequests, HttpMethod.POST, uriTemplate.expand());

        ResponseEntity<Set<String>> responseEntity = restTemplate.exchange(listRequestEntity, RESPONSE_TYPE_SET);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            log.warn("Noe skjedde under lagring i dkif, sjekk dkif-forvalter loggen");
        }
        if (responseEntity.getBody() != null) {
            if (!responseEntity.getBody().isEmpty()) {
                log.warn("Kunne ikke lagre alle identer. Ikke lagrede identer {}", responseEntity.getBody());
                return responseEntity.getBody();
            }
        }
        return Collections.emptySet();
    }

}
