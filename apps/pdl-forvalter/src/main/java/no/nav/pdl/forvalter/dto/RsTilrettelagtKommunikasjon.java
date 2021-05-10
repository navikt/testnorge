package no.nav.pdl.forvalter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.pdl.forvalter.domain.PdlDbVersjon;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsTilrettelagtKommunikasjon extends PdlDbVersjon {

    @Schema(description = "Hvilket språk man trenger språktolk til. Oppgis i 2-bokstavs kode fra kodeverket Språk")
    private String spraakForTaletolk;

    @Schema(description = "Hvilket språk man trenger språktolk til. Oppgis i 2-bokstavs kode fra kodeverket Språk")
    private String spraakForTegnspraakTolk;
}
