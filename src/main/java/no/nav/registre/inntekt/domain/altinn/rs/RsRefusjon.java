package no.nav.registre.inntekt.domain.altinn.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@ApiModel
@Builder
@Value
@NoArgsConstructor(force = true)
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

    public List<RsEndringIRefusjon> getEndringIRefusjonListe() {
        return Objects.requireNonNullElse(endringIRefusjonListe, Collections.emptyList());
    }
}
