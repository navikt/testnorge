package no.nav.pdl.forvalter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.pdl.forvalter.domain.Identtype;
import no.nav.pdl.forvalter.domain.PdlKjoenn;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HentIdenterRequest {
    private Identtype identtype;
    private LocalDate foedtEtter;
    private LocalDate foedtFoer;
    private PdlKjoenn.Kjoenn kjoenn;
    @NotNull
    private int antall;
    private String rekvirertAv;
    private Boolean syntetisk;
}
