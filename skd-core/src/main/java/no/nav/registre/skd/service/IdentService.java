package no.nav.registre.skd.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import no.nav.registre.skd.consumer.IdentPoolConsumer;
import no.nav.registre.skd.consumer.TpsfConsumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdentService {

    private final TpsfConsumer tpsfConsumer;
    private final IdentPoolConsumer identPoolConsumer;

    public List<Long> slettIdenterFraAvspillergruppe(
            Long avspillergruppeId,
            List<String> miljoer,
            List<String> identer
    ) {
        tpsfConsumer.slettIdenterFraTps(miljoer, identer);
        var meldingIderTilhoerendeIdenter = tpsfConsumer.getMeldingIderTilhoerendeIdenter(avspillergruppeId, identer);
        var tpsfResponse = tpsfConsumer.slettMeldingerFraTpsf(meldingIderTilhoerendeIdenter);
        if (tpsfResponse.getStatusCode().is2xxSuccessful()) {
            List<String> frigjorteIdenter = identPoolConsumer.frigjoerLedigeIdenter(identer);
            log.info("Identer som ble frigjort i ident-pool: {}", frigjorteIdenter.toString());
            return meldingIderTilhoerendeIdenter;
        } else {
            return new ArrayList<>();
        }
    }
}
