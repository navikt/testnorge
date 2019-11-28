package no.nav.registre.inntektsmeldingstub.service.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

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

    public Optional<Boolean> getHarUtbetaltPliktigeDager() { return Optional.ofNullable(harUtbetaltPliktigeDager); }
    public Optional<List<RsPeriode>> getFravaersPerioder() { return Optional.ofNullable(fravaersPerioder); }
    public Optional<List<RsDelvisFravaer>> getDelvisFravaersListe() { return Optional.ofNullable(delvisFravaersListe); }
}
