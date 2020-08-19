package no.nav.registre.inntekt.consumer.rs.altinninntekt.dto.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@ApiModel
@Builder
@Value
@NoArgsConstructor(force = true)
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

    public List<RsPeriode> getFravaersPerioder() {
        return Objects.requireNonNullElse(fravaersPerioder, Collections.emptyList());
    }

    public List<RsDelvisFravaer> getDelvisFravaersListe() {
        return Objects.requireNonNullElse(delvisFravaersListe, Collections.emptyList());
    }
}
