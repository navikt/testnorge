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
public class TpsSyntPakkenService {

    @Autowired
    private TpsfConsumer tpsfConsumer;

    @Autowired
    private HodejegerenConsumer hodejegerenConsumer;

    public AvspillingResponse produserOgSendSkdmeldingerTilTpsIMiljoer(Long skdMeldingGruppeId,
            String miljoe,
            Map<String, Integer> antallMeldingerPerEndringskode) {

        List<Long> ids = new ArrayList<>();
        ids.addAll(hodejegerenConsumer.startSyntetisering(new GenereringsOrdreRequest(skdMeldingGruppeId, miljoe, antallMeldingerPerEndringskode)));

        return tpsfConsumer.sendSkdmeldingerTilTps(skdMeldingGruppeId, new SendToTpsRequest(miljoe, ids));
    }
}
