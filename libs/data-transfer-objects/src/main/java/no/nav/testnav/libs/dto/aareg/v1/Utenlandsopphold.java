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
@Schema(description = "Informasjon om utenlandsopphold")
public class Utenlandsopphold {

    @Schema(description = "Periode for utenlandsopphold")
    private Periode periode;

    @Schema(description = "Landkode (kodeverk: Landkoder)", example = "JPN")
    private String landkode;

    private YearMonth rapporteringsperiode;
}
