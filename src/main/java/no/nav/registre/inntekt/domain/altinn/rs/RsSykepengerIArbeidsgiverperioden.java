package no.nav.registre.inntekt.domain.altinn.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@ApiModel
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RsSykepengerIArbeidsgiverperioden {

    @JsonProperty
    @ApiModelProperty()
    private List<RsPeriode> arbeidsgiverperiodeListe;
    @JsonProperty
    @ApiModelProperty("Brutto utbetalt sykepenger")
    private Double bruttoUtbetalt;
    @JsonProperty
    @ApiModelProperty("Begrunnelse for reduksjon eller ikke utbetalt")
    private String begrunnelseForReduksjonEllerIkkeUtbetalt;

}
