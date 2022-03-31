package no.nav.pdl.forvalter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FolkeregisterPersonstatus extends DbVersjonDTO {

    private FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus status;
}
