package no.nav.testnav.libs.dto.pdlforvalter.v1;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class TilrettelagtKommunikasjonDTO extends DbVersjonDTO {

    @Schema(description = "Hvilket språk man trenger språktolk til. Oppgis i 2-bokstavs kode fra kodeverket Språk")
    private String spraakForTaletolk;

    @Schema(description = "Hvilket språk man trenger språktolk til. Oppgis i 2-bokstavs kode fra kodeverket Språk")
    private String spraakForTegnspraakTolk;
}
