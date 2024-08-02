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
    private final static DatoIntervall FOERSTE_INTERVALL= DatoIntervall.builder()
            .from(LocalDate.now().minusYears(24))
            .tom(LocalDate.now().minusYears(18))
            .build();
    private final static DatoIntervall ANDRE_INTERVALL = DatoIntervall.builder()
            .from(LocalDate.now().minusYears(39))
            .tom(LocalDate.now().minusYears(25))
            .build();
    private final static DatoIntervall TREDJE_INTERVALL = DatoIntervall.builder()
            .from(LocalDate.now().minusYears(54))
            .tom(LocalDate.now().minusYears(40))
            .build();
    private final static DatoIntervall FJERDE_INTERVALL = DatoIntervall.builder()
            .from(LocalDate.now().minusYears(66))
            .tom(LocalDate.now().minusYears(55))
            .build();
    private final static DatoIntervall FEMTE_INTERVALL = DatoIntervall.builder()
            .from(LocalDate.now().minusYears(72))
            .tom(LocalDate.now().minusYears(67))
            .build();

    public List<DatoIntervall> getDatoListe(){
        List<DatoIntervall> alderspennList = new ArrayList<>();
        initialiserDatoListe(alderspennList);
        return alderspennList;
    }

    public static void initialiserDatoListe(List<DatoIntervall> alderspennList) {
        alderspennList.add(FOERSTE_INTERVALL);
        alderspennList.add(ANDRE_INTERVALL);
        alderspennList.add(TREDJE_INTERVALL);
        alderspennList.add(FJERDE_INTERVALL);
        alderspennList.add(FEMTE_INTERVALL);
    }
}
