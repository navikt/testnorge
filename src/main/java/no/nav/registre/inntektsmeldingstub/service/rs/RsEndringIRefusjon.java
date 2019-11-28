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
public class RsEndringIRefusjon {

    @JsonProperty
    @ApiModelProperty(example = "YYYY-MM-DD")
    private LocalDate endringsdato;
    @JsonProperty
    @ApiModelProperty("Nytt refusjonsbeløp per måned")
    private Double refusjonsbeloepPrMnd;

    public Optional<LocalDate> getEndringsdato() { return Optional.ofNullable(endringsdato); }
    public Optional<Double> getRefusjonsbeloepPrMnd() { return Optional.ofNullable(refusjonsbeloepPrMnd); }
}
