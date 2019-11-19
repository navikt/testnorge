package no.nav.dolly.domain.resultset.aareg;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsPermisjon {

    @ApiModelProperty(
            position = 1
    )
    private String permisjonsId;

    @ApiModelProperty(
            required = true,
            position = 2
    )
    private RsPeriode permisjonsPeriode;

    @ApiModelProperty(
            required = true,
            position = 3
    )
    private BigDecimal permisjonsprosent;

    @ApiModelProperty(
            value = "Gyldige verdier finnes i kodeverk 'PermisjonsOgPermitteringsBeskrivelse'",
            required = true,
            position = 4
    )
    private String permisjonOgPermittering;
}
