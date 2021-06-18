package no.nav.pdl.forvalter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import no.nav.pdl.forvalter.domain.DbVersjonDTO;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PdlUtflytting extends DbVersjonDTO {

    private Folkeregistermetadata folkeregistermetadata;
    private String tilflyttingsland;
    private String tilflyttingsstedIUtlandet;
}