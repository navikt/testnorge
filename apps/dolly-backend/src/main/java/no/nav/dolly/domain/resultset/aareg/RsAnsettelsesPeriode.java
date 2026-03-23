package no.nav.dolly.domain.resultset.aareg;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsAnsettelsesPeriode {

    @Schema(description = "Dato fra-og-med",
            type = "LocalDateTime",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime fom;

    @Schema(description = "Dato til-og-med",
            type = "LocalDateTime")
    private LocalDateTime tom;

    @Schema(description = "Årsak for avsluttet ansettelsesperiode (kodeverk: SluttÅrsakAareg)",
            example = "arbeidstakerHarSagtOppSelv",
            type = "String")
    private String sluttaarsak;
}
