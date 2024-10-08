package no.nav.testnav.libs.dto.levendearbeidsforhold.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.testnav.libs.dto.levendearbeidsforhold.v1.util.TimeUtil;

import java.time.YearMonth;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "periode",
        "landkode",
        "rapporteringsperiode",
        "sporingsinformasjon"
})
@Schema(description = "Informasjon om utenlandsopphold")
public class Utenlandsopphold {

    @Schema(description = "Periode for utenlandsopphold")
    private Periode periode;

    @Schema(description = "Landkode (kodeverk: Landkoder)", example = "JPN")
    private String landkode;

    private YearMonth rapporteringsperiode;

    @Schema(description = "Informasjon om opprettelse og endring av objekt")
    private Sporingsinformasjon sporingsinformasjon;

    @JsonIgnore
    public YearMonth getRapporteringsperiode() {
        return rapporteringsperiode;
    }

    @JsonProperty("rapporteringsperiode")
    @Schema(description = "Rapporteringsperiode for utenlandsopphold, format (ISO-8601): yyyy-MM", example = "2017-12")
    public String getRapporteringsperiodeAsString() {
        return TimeUtil.toString(rapporteringsperiode);
    }

    @JsonProperty("rapporteringsperiode")
    public void setRapporteringsperiodeAsString(String rapporteringsperiode) {
        this.rapporteringsperiode = TimeUtil.toYearMonth(rapporteringsperiode);
    }
}
