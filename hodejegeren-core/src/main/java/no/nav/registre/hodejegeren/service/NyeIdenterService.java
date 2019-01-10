package no.nav.registre.hodejegeren.service;

import static no.nav.registre.hodejegeren.service.utilities.RedigereSkdmeldingerUtility.opprettStatsborgerendringsmelding;
import static no.nav.registre.hodejegeren.service.utilities.RedigereSkdmeldingerUtility.putFnrInnIMelding;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

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
        int antallNyeIdenter = meldinger.size();
        HentIdenterRequest request = HentIdenterRequest.builder()
                .antall(antallNyeIdenter)
                .identtype(identType)
                .foedtEtter(LocalDate.now().minusYears(90)).build();
        List<String> identer = identPoolConsumer.hentNyeIdenter(request);
        for (int i = 0; i < antallNyeIdenter; i++) {
            putFnrInnIMelding((RsMeldingstype1Felter) meldinger.get(i), identer.get(i));
            if (Endringskoder.INNVANDRING.getAarsakskode().equals(meldinger.get(i).getAarsakskode())) {
                meldinger.add(opprettStatsborgerendringsmelding((RsMeldingstype1Felter) meldinger.get(i)));
            }
        }
        return identer;
    }
}
