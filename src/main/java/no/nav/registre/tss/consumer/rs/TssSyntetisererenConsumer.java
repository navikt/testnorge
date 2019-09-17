package no.nav.registre.tss.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.ArrayList;
import java.util.List;

import no.nav.registre.tss.domain.Person;

@Component
@Slf4j
public class TssSyntetisererenConsumer {

    private static final ParameterizedTypeReference<List<String>> RESPONSE_TYPE = new ParameterizedTypeReference<List<String>>() {
    };
    
    private RestTemplate restTemplate;

    private UriTemplate url;

    public TssSyntetisererenConsumer(RestTemplateBuilder restTemplateBuilder,
            @Value("${synthdata-tss-api-url}") String synthdataTssUrl) {
        this.restTemplate = restTemplateBuilder.build();
        this.url = new UriTemplate(synthdataTssUrl + "/v1/generate_tss_file");
    }

    public List<String> hentSyntetiskeTssRutiner(List<Person> identer) {

        RequestEntity postRequest = RequestEntity.post(url.expand()).body(identer);

        List<String> rutiner = new ArrayList<>();

        ResponseEntity<List<String>> response = restTemplate.exchange(postRequest, RESPONSE_TYPE);
        if (response.getBody() != null) {
            rutiner.addAll(response.getBody());
        } else {
            log.error("Kunne ikke hente responses body fra synthdata-tss: NullPointerException");
        }

        return rutiner;
    }
}
