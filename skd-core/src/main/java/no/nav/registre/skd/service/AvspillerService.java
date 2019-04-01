package no.nav.registre.skd.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import no.nav.registre.skd.consumer.TpsfConsumer;
import no.nav.registre.skd.consumer.requests.SendToTpsRequest;
import no.nav.registre.skd.consumer.response.SkdMeldingerTilTpsRespons;

@Service
public class AvspillerService {

    @Autowired
    private TpsfConsumer tpsfConsumer;

    public SkdMeldingerTilTpsRespons startAvspillingAvTpsfAvspillergruppe(Long avspillergruppeId, String miljoe) {
        List<Long> meldingIdsFromAvspillergruppe = tpsfConsumer.getMeldingIdsFromAvspillergruppe(avspillergruppeId);
        SendToTpsRequest sendToTpsRequest = new SendToTpsRequest(miljoe, meldingIdsFromAvspillergruppe);
        return tpsfConsumer.sendSkdmeldingerToTps(avspillergruppeId, sendToTpsRequest);
    }
}
