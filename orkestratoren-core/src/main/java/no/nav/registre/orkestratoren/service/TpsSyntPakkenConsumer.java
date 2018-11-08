package no.nav.registre.orkestratoren.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.nav.registre.orkestratoren.consumer.rs.HodejegerenConsumer;
import no.nav.registre.orkestratoren.consumer.rs.TpsfConsumer;
import no.nav.registre.orkestratoren.consumer.rs.requests.GenereringsOrdreRequest;
import no.nav.registre.orkestratoren.consumer.rs.requests.SendToTpsRequest;
import no.nav.registre.orkestratoren.consumer.rs.response.AvspillingResponse;

@Service
public class TpsSyntPakkenConsumer {

    @Autowired
    private TpsfConsumer tpsfConsumer;

    @Autowired
    private HodejegerenConsumer hodejegerenConsumer;

    public AvspillingResponse produserOgSendSkdmeldingerTilTpsIMiljoer(long skdMeldingGruppeId,
            String miljoe,
            Map<String, Integer> antallMeldingerPerAarsakskode) {

        List<Long> ids = new ArrayList<>();
        ids.addAll(hodejegerenConsumer.startSyntetisering(new GenereringsOrdreRequest(skdMeldingGruppeId, miljoe, antallMeldingerPerAarsakskode)));

        return tpsfConsumer.sendSkdMeldingTilTpsf(skdMeldingGruppeId, new SendToTpsRequest(miljoe, ids));
    }
}
