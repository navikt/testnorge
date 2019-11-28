package no.nav.registre.inntektsmeldingstub.service.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Optional;

@ApiModel
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RsDelvisFravaer {

    @JsonProperty
    @ApiModelProperty(example = "YYYY-MM-DD")
    private LocalDate dato;
    @JsonProperty
    @ApiModelProperty("Antall timer delvis fravær")
    private Double timer;

    public Optional<LocalDate> getDato() { return Optional.ofNullable(dato); }
    public Optional<Double> getTimer() { return Optional.ofNullable(timer); }
}
