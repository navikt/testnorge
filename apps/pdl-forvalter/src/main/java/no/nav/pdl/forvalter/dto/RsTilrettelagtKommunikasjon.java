package no.nav.pdl.forvalter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import no.nav.pdl.forvalter.domain.PdlDbVersjon;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class RsTilrettelagtKommunikasjon extends PdlDbVersjon {

    @Schema(description = "Hvilket språk man trenger språktolk til. Oppgis i 2-bokstavs kode fra kodeverket Språk")
    private String spraakForTaletolk;

    @Schema(description = "Hvilket språk man trenger språktolk til. Oppgis i 2-bokstavs kode fra kodeverket Språk")
    private String spraakForTegnspraakTolk;
}
