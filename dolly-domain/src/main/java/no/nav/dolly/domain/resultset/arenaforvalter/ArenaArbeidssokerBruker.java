package no.nav.dolly.domain.resultset.arenaforvalter;

import static java.util.Objects.isNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArenaArbeidssokerBruker {

    private List<Arbeidssoker> arbeidsokerList;
    private Long antallSider;

    public List<Arbeidssoker> getArbeidsokerList() {

        if (isNull(arbeidsokerList)) {
            arbeidsokerList = new ArrayList<>();
        }
        return arbeidsokerList;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Arbeidssoker {

        private String personident;
        private String miljoe;
        private String status;
        private String eier;
        private String servicebehov;
        private String automatiskInnsendingAvMeldekort;
    }
}
