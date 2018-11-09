package no.nav.registre.hodejegeren.service;

import static no.nav.registre.hodejegeren.service.EksisterendeIdenterService.getFoedselsdatoFraFnr;
import static no.nav.registre.hodejegeren.service.utilities.RedigereSkdmeldingerUtility.putFnrInnIMelding;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.hodejegeren.consumer.IdentPoolConsumer;
import no.nav.registre.hodejegeren.consumer.requests.HentIdenterRequest;
import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype;
import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype1Felter;

@Service
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class FoedselService {

    @Autowired
    private IdentPoolConsumer identPoolConsumer;

    private Random rand;

    public FoedselService(Random rand) {
        this.rand = rand;
    }

    public List<String> behandleFoedselsmeldinger(HentIdenterRequest.IdentType identType, List<RsMeldingstype> meldinger, List<String> levendeIdenterINorge) {
        List<String> moedre = findMoedre(meldinger.size(), levendeIdenterINorge);
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

            barn.add(barnFnr);
        }

        return barn;
    }

    public List<String> findMoedre(int antallNyeIdenter, List<String> levendeIdenterINorge) {
        List<String> moedre = new ArrayList<>(antallNyeIdenter);

        for (int i = 0; i < antallNyeIdenter; i++) {
            moedre.add(levendeIdenterINorge.get(rand.nextInt(levendeIdenterINorge.size())));
        }

        return moedre;
    }
}
