package no.nav.pdl.forvalter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KjoennDTO;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HentIdenterRequest {

    public enum Identtype {
        FNR,
        DNR,
        BOST
    }

    private Identtype identtype;
    private LocalDate foedtEtter;
    private LocalDate foedtFoer;
    private KjoennDTO.Kjoenn kjoenn;

    @NotNull
    private int antall;
    private String rekvirertAv;
    private Boolean syntetisk;
}
