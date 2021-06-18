package no.nav.pdl.forvalter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.DbVersjonDTO;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PdlInnflytting extends DbVersjonDTO {

    private Folkeregistermetadata folkeregistermetadata;
    private String fraflyttingsland;
    private String fraflyttingsstedIUtlandet;
}