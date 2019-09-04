package no.nav.registre.tss.consumer.rs.response;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate url;

    public TssSyntetisererenConsumer(@Value("${synthdata-tss-api-url}") String synthdataTssUrl){
        this.url = new UriTemplate(synthdataTssUrl + "/v1/generate_tss_file");
    }

    public List<String> produceFilesToTSS(List<Person> personerToInsert) {

        RequestEntity postRequest = RequestEntity.post(url.expand()).body(personerToInsert);

        List<String> filesToInsertInTSS = new ArrayList<>();

        ResponseEntity<List<String>> response = restTemplate.exchange(postRequest, RESPONSE_TYPE);
        if (response.getBody() != null) {
            filesToInsertInTSS.addAll(response.getBody());
        } else {
            log.error("Kunne ikke hente response body fra synthdata-tss: NullPointerException");
        }

        return filesToInsertInTSS;
    }

}
