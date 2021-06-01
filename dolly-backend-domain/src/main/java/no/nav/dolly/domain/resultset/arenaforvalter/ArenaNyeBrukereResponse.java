package no.nav.dolly.domain.resultset.arenaforvalter;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArenaNyeBrukereResponse {

    public enum BrukerFeilstatus {DUPLIKAT, MILJOE_IKKE_STOETTET, FINNES_ALLEREDE_PAA_VALGT_MILJO, BRUKEREN_ER_IKKE_REGISTRERT, AKTIVER_BRUKER, INAKTIVER_BRUKER, AKTIVER_AAP_115, AKTIVER_AAP}

    private List<Bruker> arbeidsokerList;
    private List<NyBrukerFeilV1> nyBrukerFeilList;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Bruker {

        private String personident;
        private String miljoe;
        private String status;
        private String eier;
        private String servicebehov;
        private boolean automatiskInnsendingAvMeldekort;
        private boolean aap115;
        private boolean aap;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NyBrukerFeilV1 {

        private String personident;
        private String miljoe;
        private BrukerFeilstatus nyBrukerFeilstatus;
        private String melding;
    }

    public List<Bruker> getArbeidsokerList() {
        if (isNull(arbeidsokerList)) {
            arbeidsokerList = new ArrayList<>();
        }
        return arbeidsokerList;
    }

    public List<NyBrukerFeilV1> getNyBrukerFeilList() {
        if (isNull(nyBrukerFeilList)) {
            nyBrukerFeilList = new ArrayList<>();
        }
        return nyBrukerFeilList;
    }
}
