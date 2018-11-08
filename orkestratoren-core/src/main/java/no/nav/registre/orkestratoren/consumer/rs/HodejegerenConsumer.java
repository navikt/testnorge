package no.nav.registre.orkestratoren.consumer.rs;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import no.nav.registre.orkestratoren.consumer.rs.requests.GenereringsOrdreRequest;

@Component
public class HodejegerenConsumer {

    @Autowired
    private RestTemplate restTemplate;

    private String url;

    public HodejegerenConsumer(@Value("${testnorge-hodejegeren.rest-api.url}") String hodejegerenServerUrl,
            @Value("${hodejegeren.base.path}") String hodejegerenBasePath) {
        this.url = hodejegerenServerUrl + hodejegerenBasePath;
    }

    public List<Long> startSyntetisering(GenereringsOrdreRequest genereringsOrdreRequest) {
        return restTemplate.postForObject(url,
                genereringsOrdreRequest,
                ArrayList.class);
    }
}
