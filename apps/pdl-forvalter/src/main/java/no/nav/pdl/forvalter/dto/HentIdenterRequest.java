package no.nav.pdl.forvalter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.testnav.libs.dto.pdlforvalter.v1.Identtype;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KjoennDTO;

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
    private KjoennDTO.Kjoenn kjoenn;

    @NotNull
    private int antall;
    private String rekvirertAv;
    private Boolean syntetisk;
}
