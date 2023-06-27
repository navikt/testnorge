package no.nav.dolly.domain.resultset.arenaforvalter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArenaBruker {

    public enum BrukerStatus {OK, ERROR, BRUKER_EKSISTERER_ALLEREDE}

    private String personident;
    private String miljoe;
    private BrukerStatus status;
    private String eier;
    private String servicebehov;
    private String automatiskInnsendingAvMeldekort;
    private String oppfolging;
    private LocalDate aktiveringsDato;
    private Boolean aap115;
    private Boolean aap;
}
