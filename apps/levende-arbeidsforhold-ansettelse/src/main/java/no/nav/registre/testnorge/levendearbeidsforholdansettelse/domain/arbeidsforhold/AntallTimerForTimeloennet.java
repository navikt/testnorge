package no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.arbeidsforhold;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.arbeidsforhold.util.JavaTimeUtil;

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
        "antallTimer",
        "rapporteringsperiode",
        "sporingsinformasjon"
})
@Schema(description = "Informasjon om antall timer med timel&oslash;nn")
public class AntallTimerForTimeloennet {

    private Periode periode;

    @Schema(description = "Antall timer", example = "37.5")
    private Double antallTimer;

    private YearMonth rapporteringsperiode;

    private Sporingsinformasjon sporingsinformasjon;

    @JsonIgnore
    public YearMonth getRapporteringsperiode() {
        return rapporteringsperiode;
    }

    @JsonProperty("rapporteringsperiode")
    @Schema(description = "Rapporteringsperiode for antall timer med timel&oslash;nn, format (ISO-8601): yyyy-MM", example = "2018-05")
    public String getRapporteringsperiodeAsString() {
        return JavaTimeUtil.toString(rapporteringsperiode);
    }

    @JsonProperty("rapporteringsperiode")
    public void setRapporteringsperiodeAsString(String rapporteringsperiode) {
        this.rapporteringsperiode = JavaTimeUtil.toYearMonth(rapporteringsperiode);
    }
}
