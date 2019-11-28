package no.nav.registre.inntektsmeldingstub.service.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ApiModel
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RsRefusjon {

    @JsonProperty
    @ApiModelProperty("Samlet månedlig beløp på refusjon")
    private Double refusjonsbeloepPrMnd;
    @JsonProperty
    @ApiModelProperty(value = "Opphørsdato for refusjon", example = "YYYY-MM-DD")
    private LocalDate refusjonsopphoersdato;
    @JsonProperty
    @ApiModelProperty()
    private List<RsEndringIRefusjon> endringIRefusjonListe;

    public Optional<Double> getRefusjonsbeloepPrMnd() { return Optional.ofNullable(refusjonsbeloepPrMnd); }
    public Optional<LocalDate> getRefusjonsopphoersdato() { return Optional.ofNullable(refusjonsopphoersdato); }
    public Optional<List<RsEndringIRefusjon>> getEndringIRefusjonListe() { return Optional.ofNullable(endringIRefusjonListe); }
}
