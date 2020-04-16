package no.nav.dolly.domain.resultset.aareg;

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
public class RsUtenlandsopphold {

    @ApiModelProperty(
            position = 1,
            required = true
    )
    private RsPeriode periode;

    @ApiModelProperty(
            value = "Gyldige verdier finnes i kodeverk 'LandkoderISO2'",
            required = true,
            position = 2
    )
    private String land;
}
