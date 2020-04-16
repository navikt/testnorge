package no.nav.dolly.domain.resultset.arenaforvalter;

import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
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
