package no.nav.dolly.domain.resultset.aareg;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsPermisjon {

    @Schema(required = true)
    private RsPeriodeAareg permisjonsPeriode;

    @Schema(required = true)
    private BigDecimal permisjonsprosent;

    @Schema(description = "Gyldige verdier finnes i kodeverk 'PermisjonsOgPermitteringsBeskrivelse'",
            required = true)
    private String permisjon;

    private String permisjonOgPermittering;
    private String permisjonId;
}
