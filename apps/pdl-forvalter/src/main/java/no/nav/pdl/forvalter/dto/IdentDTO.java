package no.nav.pdl.forvalter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class IdentDTO extends DbVersjonDTO {

    private String ident;
}
