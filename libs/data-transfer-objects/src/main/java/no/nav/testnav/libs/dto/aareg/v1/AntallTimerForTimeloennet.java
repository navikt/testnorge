package no.nav.testnav.libs.dto.aareg.v1;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.YearMonth;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Informasjon om antall timer med timelønn")
public class AntallTimerForTimeloennet {

    @Schema(description = "Periode for antall timer med timelønn")
    private Periode periode;

    @Schema(description = "Antall timer", example = "37.5")
    private Double antallTimer;

    private YearMonth rapporteringsperiode;
}
