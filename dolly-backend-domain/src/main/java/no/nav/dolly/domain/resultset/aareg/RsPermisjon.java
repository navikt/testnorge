package no.nav.dolly.domain.resultset.aareg;

import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonInclude;

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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsPermisjon {

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
