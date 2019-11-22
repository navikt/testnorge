package no.nav.registre.inntektsmeldingstub.service.rs;

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
public class RsDelvisFravaer {

    @JsonProperty
    @ApiModelProperty(example = "YYYY-MM-DD")
    private LocalDate dato;
    @JsonProperty
    @ApiModelProperty("Antall timer delvis fravær")
    private double timer;

}
