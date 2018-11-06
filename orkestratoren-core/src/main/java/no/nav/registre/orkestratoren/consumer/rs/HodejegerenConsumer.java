package no.nav.registre.orkestratoren.consumer.rs;

import no.nav.registre.orkestratoren.consumer.rs.requests.GenereringsOrdreRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
public class HodejegerenConsumer {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${hodejegeren.server.url}")
    private String hodejegerenServerUrl;

    @Value("${hodejegeren.base.url}")
    private String hodejegerenBaseUrl;

    public List<Long> startSyntetisering(GenereringsOrdreRequest genereringsOrdreRequest) {
        String url = hodejegerenServerUrl + hodejegerenBaseUrl;

        return restTemplate.postForObject(url,
                genereringsOrdreRequest,
                ArrayList.class);
    }
}
