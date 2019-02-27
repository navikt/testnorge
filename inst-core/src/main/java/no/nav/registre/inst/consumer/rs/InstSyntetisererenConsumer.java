package no.nav.registre.inst.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InstSyntetisererenConsumer {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${syntrest.rest.inst.api.url}")
    private String url;

    public List<Map<String, String>> hentInstMeldingerFromSyntRest(int numToGenerate) {
       List<Map<String, String>> response = restTemplate.getForObject(String.format(url, numToGenerate), List.class);
       return response;
    }
}
