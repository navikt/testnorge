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
public class RsNaturaYtelseDetaljer {

    @JsonProperty
    @ApiModelProperty()
    private String naturaytelseType;
    @JsonProperty
    @ApiModelProperty(value = "Startdato for naturaytelse", example = "YYYY-MM-DD")
    private LocalDate fom;
    @JsonProperty
    @ApiModelProperty("Samlet månedlig beløp for naturaytelsen")
    private Double beloepPrMnd;

    public Optional<String> getNaturaytelseType() { return Optional.ofNullable(naturaytelseType); }
    public Optional<LocalDate> getFom() { return Optional.ofNullable(fom); }
    public Optional<Double> getBeloepPrMnd() { return Optional.ofNullable(beloepPrMnd); }
}
