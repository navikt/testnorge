package no.nav.testnav.libs.domain.dto.aordningen.arbeidsforhold;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.YearMonth;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({ "periode", "antallTimer", "rapporteringsperiode", "sporingsinformasjon" })
@ApiModel(
        description = "Informasjon om antall timer med timel&oslash;nn"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class AntallTimerForTimeloennet {

    @ApiModelProperty(
            notes = "Periode for antall timer med timel&oslash;nn"
    )
    private Periode periode;
    @ApiModelProperty(
            notes = "Antall timer",
            example = "37.5"
    )
    private Double antallTimer;
    private YearMonth rapporteringsperiode;
    @ApiModelProperty(
            notes = "Informasjon om opprettelse og endring av objekt"
    )
    private Sporingsinformasjon sporingsinformasjon;

    @JsonProperty("rapporteringsperiode")
    @ApiModelProperty(
            notes = "Rapporteringsperiode for antall timer med timel&oslash;nn, format (ISO-8601): yyyy-MM",
            example = "2018-05"
    )
    public String getRapporteringsperiodeAsString() {
        return JavaTimeUtil.toString(this.rapporteringsperiode);
    }
}
