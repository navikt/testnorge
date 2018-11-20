package no.nav.registre.orkestratoren.consumer.rs;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ArenaInntektSyntConsumer {

    RestTemplate restTemplate;
    private String url;

    public ArenaInntektSyntConsumer(@Value("${synthdata-arena-inntekt.rest-api.url}") String baseUrl) {
        this.url = baseUrl + "/v1/syntetisering/generer";
        this.restTemplate = new RestTemplate();
    }

    public void genererEnInntektsmeldingPerFnrIInntektstub(List<String> fnr) {
        restTemplate.postForObject(url, fnr, String.class);
    }
}
