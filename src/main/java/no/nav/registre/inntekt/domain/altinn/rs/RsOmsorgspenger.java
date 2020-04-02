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
public class RsOmsorgspenger {

    @JsonProperty
    @ApiModelProperty()
    private Boolean harUtbetaltPliktigeDager;
    @JsonProperty
    @ApiModelProperty()
    private List<RsPeriode> fravaersPerioder;
    @JsonProperty
    @ApiModelProperty()
    private List<RsDelvisFravaer> delvisFravaersListe;

}
