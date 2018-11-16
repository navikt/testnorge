package no.nav.registre.orkestratoren.consumer.rs;

import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ArenaInntektSyntConsumer {

    private static final String url = "https://synth-arena-inntekt-{}.nais.preprod.local/api/v1/syntetisering/generer";
    RestTemplate restTemplate = new RestTemplate();

    public void genererEnInntektsmeldingPerFnrIInntektstub(List<String> fnr, String miljoe) {
        restTemplate.postForObject(url, fnr, String.class, miljoe);
    }
}
