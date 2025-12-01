package no.nav.pdl.forvalter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.dto.pdlforvalter.v1.Identtype;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KjoennDTO;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentpoolStatusDTO {

    public enum Rekvireringsstatus {LEDIG, I_BRUK}

    private Identtype identtype;
    private Boolean syntetisk;
    private String personidentifikator;
    private Rekvireringsstatus rekvireringsstatus;

    private Boolean finnesHosSkatt;
    private LocalDate foedselsdato;
    private KjoennDTO.Kjoenn kjoenn;

    private String rekvirertAv;
}
