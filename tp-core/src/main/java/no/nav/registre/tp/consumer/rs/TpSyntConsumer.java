package no.nav.registre.tp.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedList;
import java.util.List;

import no.nav.registre.tp.database.models.TYtelse;

@Slf4j
@Component
public class TpSyntConsumer {

    public static final ParameterizedTypeReference<List<TYtelse>> RESPONSE_TYPE = new ParameterizedTypeReference<List<TYtelse>>() {
    };
    @Autowired
    private RestTemplate restTemplate;
    private String syntRestApiUrl;

    public TpSyntConsumer(@Value("${synt.rest.api.url}") String syntRestApiUrl) {
        this.syntRestApiUrl = syntRestApiUrl + "/v1/generate/tp?numToGenerate={numToGenerate}";
    }

    public List<TYtelse> getYtelser(int numToGenerate) {
        ResponseEntity<List<TYtelse>> responseEntity = restTemplate.exchange(syntRestApiUrl, HttpMethod.GET, null, RESPONSE_TYPE, numToGenerate);
        List<TYtelse> ytelser = new LinkedList<>();
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            ytelser = responseEntity.getBody();
        }
        return ytelser;
    }

}
