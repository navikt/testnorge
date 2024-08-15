package no.nav.registre.testnorge.levendearbeidsforholdansettelse.service.util;

import lombok.Getter;
import lombok.Setter;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.DatoIntervall;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe for å initialere alderspennlisten for sannsynlighetsfordelingen
 */
@Getter
@Setter
public class AlderspennList {
    //Dette er tallene jeg fant fra: https://www.ssb.no/arbeid-og-lonn/sysselsetting/statistikk/antall-arbeidsforhold-og-lonn
    // i tabell 1
    public final List<Double> sannsynlighetFordeling = List.of(434106.0, 1022448.0, 976833.0, 563804.0, 72363.0);
    private static final List<List<Integer>> alderListe = List.of(
            List.of(18, 24),
            List.of(25,39),
            List.of(40, 54),
            List.of(55, 66),
            List.of(67, 72)
    );


    public List<DatoIntervall> getDatoListe(){
        List<DatoIntervall> alderspennList = new ArrayList<>();
        for(List<Integer> alderSpenn :alderListe){
            alderspennList.add(DatoIntervall.builder()
                    .tom(LocalDate.now().minusYears(alderSpenn.getFirst()))
                    .from(LocalDate.now().minusYears(alderSpenn.getLast())).build());
        }
        return alderspennList;
    }
}
