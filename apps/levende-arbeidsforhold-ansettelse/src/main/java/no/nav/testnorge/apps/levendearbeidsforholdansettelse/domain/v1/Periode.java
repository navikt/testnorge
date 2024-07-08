package no.nav.testnorge.apps.levendearbeidsforholdansettelse.domain.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;
import no.nav.registre.testnorge.levendearbeidsforhold.domain.v1.util.JavaTimeUtil;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "fom",
        "tom"
})
@Schema(description = "Inneholder informasjon om periode")
public class Periode {

    private LocalDate fom;

    private LocalDate tom;

    @JsonIgnore
    public LocalDate getFom() {
        return fom;
    }

    @JsonIgnore
    public LocalDate getTom() {
        return tom;
    }

    @JsonProperty("fom")
    @Schema(description = "Fra-og-med-dato for periode, format (ISO-8601): yyyy-MM-dd", example = "2014-07-01")
    public String getFomAsString() {
        return JavaTimeUtil.toString(fom);
    }

    @JsonProperty("fom")
    public void setFomAsString(String fom) {
        this.fom = JavaTimeUtil.toLocalDate(fom);
    }

    @JsonProperty("tom")
    @Schema(description = "Til-og-med-dato for periode, format (ISO-8601): yyyy-MM-dd", example = "2015-12-31")
    public String getTomAsString() {
        return JavaTimeUtil.toString(tom);
    }

    @JsonProperty("tom")
    public void setTomAsString(String tom) {
        this.tom = JavaTimeUtil.toLocalDate(tom);
    }

    @Override
    @SuppressWarnings({"pmd:ConsecutiveLiteralAppends", "pmd:ConsecutiveAppendsShouldReuse", "fb-contrib:UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "pmd:AppendCharacterWithChar"})
    public String toString() {
        return "Periode{" + "fom=" + getFomAsString() + ", tom=" + getTomAsString() + "}";
    }
}
