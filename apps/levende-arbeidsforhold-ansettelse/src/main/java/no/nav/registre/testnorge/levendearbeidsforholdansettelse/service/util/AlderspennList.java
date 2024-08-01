package no.nav.registre.testnorge.levendearbeidsforholdansettelse.service.util;

import lombok.Getter;
import lombok.Setter;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.DatoIntervall;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AlderspennList {
    public final List<Double> sannsynlighetFordeling = List.of(434106.0, 1022448.0, 976833.0, 563804.0, 72363.0);
    private final static DatoIntervall FOERSTE_INTERVALL= DatoIntervall.builder()
            .tom(LocalDate.now().minusYears(24))
            .from(LocalDate.now().minusYears(18))
            .build();
    private final static DatoIntervall ANDRE_INTERVALL = DatoIntervall.builder()
            .tom(LocalDate.now().minusYears(39))
            .from(LocalDate.now().minusYears(25))
            .build();
    private final static DatoIntervall TREDJE_INTERVALL = DatoIntervall.builder()
            .tom(LocalDate.now().minusYears(54))
            .from(LocalDate.now().minusYears(40))
            .build();
    private final static DatoIntervall FJERDE_INTERVALL = DatoIntervall.builder()
            .tom(LocalDate.now().minusYears(66))
            .from(LocalDate.now().minusYears(55))
            .build();
    private final static DatoIntervall FEMTE_INTERVALL = DatoIntervall.builder()
            .tom(LocalDate.now().minusYears(72))
            .from(LocalDate.now().minusYears(67))
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
