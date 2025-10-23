package no.nav.pdl.forvalter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HentIdenterRequest {

    private Identtype identtype;
    private LocalDate foedtEtter;
    private LocalDate foedtFoer;
    private Kjoenn kjoenn;

    @NotNull
    private int antall;
    private String rekvirertAv;
    private Boolean syntetisk;
    private Boolean id2032;

    public enum Identtype {
        FNR,
        DNR,
        BOST
    }

    public enum Kjoenn {
        KVINNE,
        MANN
    }
}
