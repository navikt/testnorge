package no.nav.registre.inntektsmeldingstub.service.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

@ApiModel
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RsGraderingIForeldrepenger {

    @JsonProperty
    @ApiModelProperty("Periode med foreldrepenger")
    private RsPeriode periode;
    @JsonProperty
    @ApiModelProperty("Heltall med arbeidstidsprosent")
    private Integer arbeidstidprosent;

    public Optional<RsPeriode> getPeriode() { return Optional.ofNullable(periode); }
    public Optional<Integer> getArbeidstidprosent() { return Optional.ofNullable(arbeidstidprosent); }
}
