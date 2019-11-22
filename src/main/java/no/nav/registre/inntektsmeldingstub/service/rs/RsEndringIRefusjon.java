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
public class RsEndringIRefusjon {

    @JsonProperty
    @ApiModelProperty(example = "YYYY-MM-DD")
    private LocalDate endringsdato;
    @JsonProperty
    @ApiModelProperty("Nytt refusjonsbeløp per måned")
    private double refusjonsbeloepPrMnd;

}
