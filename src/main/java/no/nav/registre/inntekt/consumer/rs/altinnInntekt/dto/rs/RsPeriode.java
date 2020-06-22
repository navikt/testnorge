package no.nav.registre.inntekt.consumer.rs.altinnInntekt.dto.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;

@ApiModel
@Builder
@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class RsPeriode {

    @JsonProperty
    @ApiModelProperty(value = "Dato fra og med", example = "YYYY-MM-DD")
    private LocalDate fom;
    @JsonProperty
    @ApiModelProperty(value = "Dato til og med", example = "YYYY-MM-DD")
    private LocalDate tom;

}
