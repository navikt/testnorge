package no.nav.registre.orkestratoren.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import no.nav.registre.orkestratoren.consumer.rs.TpsfConsumer;
import no.nav.registre.orkestratoren.consumer.rs.requests.SendToTpsRequest;
import no.nav.registre.orkestratoren.consumer.rs.response.AvspillingResponse;

@Service
public class TpsSyntPakkenConsumer {

    @Autowired
    private TpsfConsumer tpsfConsumer;

    @Value("${skd.melding.miljo}")
    private String environment;

    @Value("${skd.melding.gruppe.id}")
    private Long skdMeldingGruppeId;

    public AvspillingResponse produserOgSendSkdmeldingerTilTpsIMiljoer(List<String> miljoer,
            int antallSkdMeldinger,
            List<String> aarsakskoder) {

        List<Long> ids = new ArrayList<>();

        SendToTpsRequest sendToTpsRequest = new SendToTpsRequest(environment, ids);

        return tpsfConsumer.sendSkdMeldingTilTpsf(skdMeldingGruppeId, sendToTpsRequest);
    }
}
