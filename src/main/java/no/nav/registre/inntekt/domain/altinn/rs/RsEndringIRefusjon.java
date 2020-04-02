package no.nav.registre.inntekt.domain.altinn.rs;

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
public class RsEndringIRefusjon {

    @JsonProperty
    @ApiModelProperty(example = "YYYY-MM-DD")
    private LocalDate endringsdato;
    @JsonProperty
    @ApiModelProperty("Nytt refusjonsbeløp per måned")
    private Double refusjonsbeloepPrMnd;

}
