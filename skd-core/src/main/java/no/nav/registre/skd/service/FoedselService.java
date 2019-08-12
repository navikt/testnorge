package no.nav.registre.skd.service;

import static no.nav.registre.skd.service.utilities.IdentUtility.getFoedselsdatoFraFnr;
import static no.nav.registre.skd.service.utilities.RedigereSkdmeldingerUtility.putFnrInnIMelding;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import no.nav.registre.skd.consumer.IdentPoolConsumer;
import no.nav.registre.skd.consumer.requests.HentIdenterRequest;
import no.nav.registre.skd.exceptions.ManglerEksisterendeIdentException;
import no.nav.registre.skd.skdmelding.RsMeldingstype;
import no.nav.registre.skd.skdmelding.RsMeldingstype1Felter;

@Service
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class FoedselService {

    @Autowired
    private IdentPoolConsumer identPoolConsumer;

    @Autowired
    private Random rand;

    public List<String> behandleFoedselsmeldinger(HentIdenterRequest.IdentType identType, List<RsMeldingstype> meldinger, List<String> levendeIdenterINorge) {
        if (levendeIdenterINorge.isEmpty()) {
            throw new ManglerEksisterendeIdentException("Kunne ikke finne mor til ident for SkdMelding med meldingsnummer "
                    + meldinger.get(0).getMeldingsnrHosTpsSynt() + ". For få identer i listen levendeIdenterINorge.");
        }

        List<String> moedre = findMoedre(meldinger.size(), levendeIdenterINorge, meldinger.get(0).getMeldingsnrHosTpsSynt());
        List<String> barn = new ArrayList<>(meldinger.size());

        Iterator<RsMeldingstype> meldingIterator = meldinger.iterator();
        int i = 0;
        while (meldingIterator.hasNext()) {
            RsMeldingstype melding = meldingIterator.next();

            String morFnr = moedre.get(i++);
            LocalDate morFoedselsdato = getFoedselsdatoFraFnr(morFnr);

            String barnFnr;
            try {
                barnFnr = identPoolConsumer.hentNyeIdenter(HentIdenterRequest.builder().foedtEtter(morFoedselsdato.plusYears(13)).antall(1).identtype(identType).build()).get(0);
            } catch (RuntimeException e) {
                log.warn("Kunne ikke finne barn til mor med fnr {} - Fjernet melding med meldingsnummer {}", morFnr, melding.getMeldingsnrHosTpsSynt());
                meldingIterator.remove();
                continue;
            }

            putFnrInnIMelding((RsMeldingstype1Felter) melding, barnFnr);

            ((RsMeldingstype1Felter) melding).setMorsFodselsdato(morFnr.substring(0, 6));
            ((RsMeldingstype1Felter) melding).setMorsPersonnummer(morFnr.substring(6));

            String farFnr = findFar(morFnr, levendeIdenterINorge, moedre);
            if (farFnr != null) {
                ((RsMeldingstype1Felter) melding).setFarsFodselsdato(farFnr.substring(0, 6));
                ((RsMeldingstype1Felter) melding).setFarsPersonnummer(farFnr.substring(6));
            } else {
                log.info("Kunne ikke finne far til barn med fnr {}. morFnr: {}", barnFnr, morFnr);
            }

            barn.add(barnFnr);
        }

        return barn;
    }

    public List<String> findMoedre(int antallNyeIdenter, List<String> levendeIdenterINorge, String meldingsnrHosTpsSynt) {
        List<String> moedre = new ArrayList<>(antallNyeIdenter);
        List<String> potensielleMoedre = new ArrayList<>(levendeIdenterINorge);

        potensielleMoedre.removeIf(potensiellMor -> !"02468".contains(String.valueOf(potensiellMor.charAt(8)))); // kvinner har partall på index 8 i FNR

        if (potensielleMoedre.isEmpty()) {
            throw new ManglerEksisterendeIdentException("Kunne ikke finne mor til ident for SkdMelding med meldingsnummer "
                    + meldingsnrHosTpsSynt + ". For få kvinner i listen levendeIdenterINorge.");
        }

        for (int i = 0; i < antallNyeIdenter; i++) {
            int randomIndex = rand.nextInt(potensielleMoedre.size());
            moedre.add(potensielleMoedre.get(randomIndex));
        }

        return moedre;
    }

    /**
     * @param morsFnr
     * @param levendeIdenterINorge
     * @param moedre
     * @return Fnr til far til barn. Far er like gammel eller eldre enn mor. Far kan være mann eller kvinne.
     */
    public String findFar(String morsFnr, List<String> levendeIdenterINorge, List<String> moedre) {
        List<String> potensielleFedre = new ArrayList<>(levendeIdenterINorge);
        Collections.shuffle(potensielleFedre);
        for (String ident : potensielleFedre) {
            if (!moedre.contains(ident) && getFoedselsdatoFraFnr(ident).compareTo(getFoedselsdatoFraFnr(morsFnr)) <= 0) { // finn far som er like gammel eller eldre enn mor for enkelhets skyld
                return ident;
            }
        }
        return null;
    }
}
