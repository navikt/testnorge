package no.nav.registre.inntekt.domain.altinn.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@ApiModel
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RsPeriode {

    @JsonProperty
    @ApiModelProperty(value = "Dato fra og med", example = "YYYY-MM-DD", dataType = "java.time.LocalDate")
    private LocalDate fom;
    @JsonProperty
    @ApiModelProperty(value = "Dato til og med", example = "YYYY-MM-DD", dataType = "java.time.LocalDate")
    private LocalDate tom;

}
