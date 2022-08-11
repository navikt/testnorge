package no.nav.testnav.apps.skdservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.skdservice.consumer.TpsfConsumer;
import no.nav.testnav.apps.skdservice.consumer.command.requests.SendToTpsRequest;
import no.nav.testnav.apps.skdservice.consumer.response.SkdMeldingerTilTpsRespons;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class FasteMeldingerService {

    private final TpsfConsumer tpsfConsumer;

    public Mono<SkdMeldingerTilTpsRespons> startAvspillingAvTpsfAvspillergruppe(
            Long avspillergruppeId,
            String miljoe) {

        return tpsfConsumer.getMeldingIdsFromAvspillergruppe(avspillergruppeId)
                .flatMap(meldingIds -> tpsfConsumer.sendSkdmeldingerToTps(avspillergruppeId, new SendToTpsRequest(miljoe, meldingIds)));
    }
}
