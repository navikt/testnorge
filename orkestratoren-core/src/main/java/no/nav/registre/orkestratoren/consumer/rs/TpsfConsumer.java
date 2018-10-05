package no.nav.registre.orkestratoren.consumer.rs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

@Component
public class TpsfConsumer {

    @Autowired
    private RestTemplate restTemplate;

    @Value( "${tpsf.server.url}")
    private String tpsfServerUrl;

    public ArrayList sendSkdMeldingTilTpfs(int skdMeldingGruppeId, SendToTpsRequest sendToTpsRequest) {

        return restTemplate.postForObject(tpsfServerUrl + "/api/v1/endringsmelding/skd/send/{skdMeldingGruppeId}",
                sendToTpsRequest,
                ArrayList.class);
    }
}
