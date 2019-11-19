package no.nav.registre.skd.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import no.nav.registre.skd.consumer.TpsfConsumer;

@Service
public class IdentService {

    @Autowired
    private TpsfConsumer tpsfConsumer;

    public List<Long> slettIdenterFraAvspillergruppe(
            Long avspillergruppeId,
            List<String> miljoer,
            List<String> identer
    ) {
        tpsfConsumer.slettIdenterFraTps(miljoer, identer);
        var meldingIderTilhoerendeIdenter = tpsfConsumer.getMeldingIderTilhoerendeIdenter(avspillergruppeId, identer);
        var tpsfResponse = tpsfConsumer.slettMeldingerFraTpsf(meldingIderTilhoerendeIdenter);
        if (tpsfResponse.getStatusCode().is2xxSuccessful()) {
            return meldingIderTilhoerendeIdenter;
        } else {
            return new ArrayList<>();
        }
    }
}
