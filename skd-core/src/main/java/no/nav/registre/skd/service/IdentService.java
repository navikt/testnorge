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

    public List<Long> slettIdenterFraAvspillergruppe(Long avspillergruppeId, List<String> miljoer, List<String> identer) {
        List<Long> meldingIderTilhoerendeIdenter = tpsfConsumer.getMeldingIderTilhoerendeIdenter(avspillergruppeId, identer);
        ResponseEntity tpsfResponse = tpsfConsumer.slettMeldingerFraTpsf(meldingIderTilhoerendeIdenter);
        if (tpsfResponse.getStatusCode().is2xxSuccessful()) {
            tpsfConsumer.slettIdenterFraTps(miljoer, identer);
            return meldingIderTilhoerendeIdenter;
        } else {
            return new ArrayList<>();
        }
    }
}
