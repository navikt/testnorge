package no.nav.registre.hodejegeren.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.nav.registre.hodejegeren.consumer.IdentPoolConsumer;
import no.nav.registre.hodejegeren.consumer.requests.HentIdenterRequest;
import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype;
import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype1Felter;

/**
 * Service for å plassere nye identer i skdmeldingene.
 */
@Service
public class NyeIdenterService {
    
    @Autowired
    private IdentPoolConsumer identPoolConsumer;
    
    public List<String> settInnNyeIdenterITrans1Meldinger(HentIdenterRequest.IdentType identType, List<RsMeldingstype> meldinger) {
        if (meldinger == null) {
            return new ArrayList<>();
        }
        int antallNyeIdenter = meldinger.size();
        List<String> identer = identPoolConsumer.hentNyeIdenter(HentIdenterRequest.builder().antall(antallNyeIdenter).identtype(identType).build());
        for (int i = 0; i < antallNyeIdenter; i++) {
            ((RsMeldingstype1Felter) meldinger.get(i)).setFodselsdato(identer.get(i).substring(0, 6));
            ((RsMeldingstype1Felter) meldinger.get(i)).setPersonnummer(identer.get(i).substring(6));
        }
        return identer;
    }
}
