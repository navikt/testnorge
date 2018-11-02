package no.nav.registre.hodejegeren.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import no.nav.registre.hodejegeren.consumer.IdentPoolConsumer;
import no.nav.registre.hodejegeren.consumer.requests.HentIdenterRequest;
import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype;
import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype1Felter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class FoedselService {

    @Autowired
    private IdentPoolConsumer identPoolConsumer;

    @Autowired
    private EksisterendeIdenterService eksisterendeIdenterService;

    private Random rand;

    public FoedselService (Random rand) {
        this.rand = rand;
    }

    public List<String> behandleFoedselsmeldinger(HentIdenterRequest.IdentType identType, List<RsMeldingstype> meldinger, List<String> levendeIdenterINorge) {
        if (meldinger == null) {
            return new ArrayList<>();
        }

        List<String> moedre = findMoedre(meldinger.size(), levendeIdenterINorge);
        List<String> barn = new ArrayList<>(meldinger.size());

        for (int i = 0; i < meldinger.size(); i++) {
            String morFnr = moedre.get(i);
            LocalDate morFoedselsdato = eksisterendeIdenterService.getFoedselsdatoFraFnr(morFnr);

            String barnFnr = identPoolConsumer.hentNyeIdenter(HentIdenterRequest.builder().foedtEtter(morFoedselsdato.plusYears(13)).antall(1).identtype(identType).build()).get(0);

            ((RsMeldingstype1Felter) meldinger.get(i)).setFodselsdato(barnFnr.substring(0, 6));
            ((RsMeldingstype1Felter) meldinger.get(i)).setPersonnummer(barnFnr.substring(6));

            ((RsMeldingstype1Felter) meldinger.get(i)).setMorsFodselsdato(morFnr.substring(0, 6));
            ((RsMeldingstype1Felter) meldinger.get(i)).setMorsPersonnummer(morFnr.substring(6));

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
