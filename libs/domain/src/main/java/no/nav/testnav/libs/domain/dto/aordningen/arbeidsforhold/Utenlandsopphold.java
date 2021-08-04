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
@JsonPropertyOrder({ "periode", "landkode", "rapporteringsperiode", "sporingsinformasjon" })
@ApiModel(
        description = "Informasjon om utenlandsopphold"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class Utenlandsopphold {

    @ApiModelProperty(
            notes = "Periode for utenlandsopphold"
    )
    private Periode periode;
    @ApiModelProperty(
            notes = "Landkode (kodeverk: Landkoder)",
            example = "JPN"
    )
    private String landkode;
    private YearMonth rapporteringsperiode;
    @ApiModelProperty(
            notes = "Informasjon om opprettelse og endring av objekt"
    )
    private Sporingsinformasjon sporingsinformasjon;

    @JsonProperty("rapporteringsperiode")
    @ApiModelProperty(
            notes = "Rapporteringsperiode for utenlandsopphold, format (ISO-8601): yyyy-MM",
            example = "2017-12"
    )
    public String getRapporteringsperiodeAsString() {
        return JavaTimeUtil.toString(this.rapporteringsperiode);
    }
}
