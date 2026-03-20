package no.nav.testnav.libs.dto.ereg.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "fom",
        "tom"
})
public class Gyldighetsperiode {

    @Schema(description = "Fra-og-med-dato for gyldighetsperiode, format (ISO-8601): yyyy-MM-dd", example = "2014-07-01")
    private LocalDate fom;

    @Schema(description = "Til-og-med-dato for gyldighetsperiode, format (ISO-8601): yyyy-MM-dd", example = "2015-12-31")
    private LocalDate tom;

    @JsonIgnore
    public LocalDate getTom() {
        return tom;
    }
}
