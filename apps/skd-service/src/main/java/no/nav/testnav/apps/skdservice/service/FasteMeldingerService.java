package no.nav.testnav.apps.skdservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.skdservice.consumer.TpsfConsumer;
import no.nav.testnav.apps.skdservice.consumer.command.requests.SendToTpsRequest;
import no.nav.testnav.apps.skdservice.consumer.response.SkdMeldingerTilTpsRespons;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FasteMeldingerService {

    private final TpsfConsumer tpsfConsumer;

    public SkdMeldingerTilTpsRespons startAvspillingAvTpsfAvspillergruppe(
            Long avspillergruppeId,
            String miljoe
    ) {
        var meldingIdsFromAvspillergruppe = tpsfConsumer.getMeldingIdsFromAvspillergruppe(avspillergruppeId);
        var sendToTpsRequest = new SendToTpsRequest(miljoe, meldingIdsFromAvspillergruppe);
        return tpsfConsumer.sendSkdmeldingerToTps(avspillergruppeId, sendToTpsRequest);
    }
}
