package no.nav.registre.inntekt.consumer.rs.altinninntekt.dto.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;
import no.nav.registre.inntekt.consumer.rs.altinninntekt.dto.enums.BegrunnelseIngenEllerRedusertUtbetalingKodeListe;

import java.util.List;

@ApiModel
@Builder
@Value
@NoArgsConstructor(force = true)
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
    private BegrunnelseIngenEllerRedusertUtbetalingKodeListe begrunnelseForReduksjonEllerIkkeUtbetalt;

}
