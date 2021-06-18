package no.nav.registre.testnorge.libs.dto.pdlforvalter.v1;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TilrettelagtKommunikasjonDTO extends DbVersjonDTO {

    @Schema(description = "Hvilket språk man trenger språktolk til. Oppgis i 2-bokstavs kode fra kodeverket Språk")
    private String spraakForTaletolk;

    @Schema(description = "Hvilket språk man trenger språktolk til. Oppgis i 2-bokstavs kode fra kodeverket Språk")
    private String spraakForTegnspraakTolk;
}
