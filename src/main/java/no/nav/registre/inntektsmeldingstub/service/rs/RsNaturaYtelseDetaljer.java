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
public class RsNaturaYtelseDetaljer {

    @JsonProperty
    @ApiModelProperty()
    private String naturaytelseType;
    @JsonProperty
    @ApiModelProperty(value = "Startdato for naturaytelse", example = "YYYY-MM-DD")
    private LocalDate fom;
    @JsonProperty
    @ApiModelProperty("Samlet månedlig beløp for naturaytelsen")
    private double beloepPrMnd;

}
