package no.nav.dolly.domain.resultset.arenaforvalter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import no.nav.dolly.bestilling.arenaforvalter.dto.ArenaResponse;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@ToString(callSuper = true)
public class ArenaNyeBrukereResponse extends ArenaResponse {

    @Builder
    public ArenaNyeBrukereResponse(HttpStatus status, String miljoe, String feilmelding, List<ArenaBruker> arbeidsokerList, List<NyBrukerFeilV1> nyBrukerFeilList) {
        super(status, miljoe, feilmelding);
        this.arbeidsokerList = arbeidsokerList;
        this.nyBrukerFeilList = nyBrukerFeilList;
    }

    public enum BrukerFeilstatus {
        DUPLIKAT, MILJOE_IKKE_STOETTET, FINNES_ALLEREDE_PAA_VALGT_MILJO,
        BRUKEREN_ER_IKKE_REGISTRERT, AKTIVER_BRUKER, INAKTIVER_BRUKER, AKTIVER_AAP_115, AKTIVER_AAP
    }

    private List<ArenaBruker> arbeidsokerList;
    private List<NyBrukerFeilV1> nyBrukerFeilList;


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NyBrukerFeilV1 {

        private String personident;
        private String miljoe;
        private BrukerFeilstatus nyBrukerFeilstatus;
        private String melding;
    }

    public List<ArenaBruker> getArbeidsokerList() {
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
