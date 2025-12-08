package no.nav.pdl.forvalter.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HendelseIdDTO {

    private String ident;
    private String hendelseId;
    private DbVersjonDTO.Master master;

    @JsonIgnore
    public boolean isPdlMaster() {

        return master == DbVersjonDTO.Master.PDL;
    }
}
