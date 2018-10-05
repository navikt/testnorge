package no.nav.registre.orkestratoren.consumer.rs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class TpsfConsumer {

    @Autowired
    private RestTemplate restTemplate;

    @Value( "${tpsf.server.url}")
    private String tpsfServerUrl;

    public List sendSkdMeldingTilTpsf(int skdMeldingGruppeId, SendToTpsRequest sendToTpsRequest) {
        String url = tpsfServerUrl + "/api/v1/endringsmelding/skd/send/" + skdMeldingGruppeId;

        return restTemplate.postForObject(url,
                sendToTpsRequest,
                List.class);
    }
}
