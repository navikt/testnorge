package no.nav.dolly.domain.resultset.aareg;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsPermisjon {

    @Schema
    private RsPeriodeAareg permisjonsPeriode;

    @Schema
    private BigDecimal permisjonsprosent;

    @Schema(description = "Gyldige verdier finnes i kodeverk 'PermisjonsOgPermitteringsBeskrivelse'",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String permisjon;

    private String permisjonOgPermittering;
    private String permisjonId;
}
