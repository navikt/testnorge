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

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "fom",
        "tom"
})
@Schema(description = "Inneholder informasjon om bruksperiode til objektet")
public class Bruksperiode {

    private LocalDateTime fom;

    private LocalDateTime tom;

    @JsonIgnore
    public LocalDateTime getFom() {
        return fom;
    }

    @JsonIgnore
    public LocalDateTime getTom() {
        return tom;
    }

    @JsonProperty("fom")
    @Schema(description = "Fra-tidsstempel for bruksperiode, format (ISO-8601): yyyy-MM-dd'T'HH:mm[:ss[.SSSSSSSSS]]", example = "2015-01-06T21:44:04.748")
    public String getFomAsString() {
        return TimeUtil.toString(fom);
    }

    @JsonProperty("fom")
    public void setFomAsString(String fom) {
        this.fom = TimeUtil.toLocalDateTime(fom);
    }

    @JsonProperty("tom")
    @Schema(description = "Til-tidsstempel for bruksperiode, format (ISO-8601): yyyy-MM-dd'T'HH:mm[:ss[.SSSSSSSSS]]", example = "2015-12-06T19:45:04")
    public String getTomAsString() {
        return TimeUtil.toString(tom);
    }

    @JsonProperty("tom")
    public void setTomAsString(String tom) {
        this.tom = TimeUtil.toLocalDateTime(tom);
    }

    @Override
    @SuppressWarnings({"pmd:ConsecutiveLiteralAppends", "pmd:ConsecutiveAppendsShouldReuse", "fb-contrib:UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "pmd:AppendCharacterWithChar"})
    public String toString() {
        return "Bruksperiode{" + "fom=" + getFomAsString() + ", tom=" + getTomAsString() + "}";
    }
}
