package no.nav.testnav.libs.dto.ereg.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "fom",
        "tom"
})
public class Bruksperiode {

    @Schema(description = "Fra-dato for bruksperiode, format (ISO-8601): yyyy-MM-dd'T'HH:mm[:ss[.SSSSSSSSS]]", example = "2015-01-06T21:44:04.748")
    private LocalDateTime fom;

    @Schema(description = "Til-dato for bruksperiode, format (ISO-8601): yyyy-MM-dd'T'HH:mm[:ss[.SSSSSSSSS]]", example = "2015-12-06T19:45:04")
    private LocalDateTime tom;

    @JsonIgnore
    public LocalDateTime getFom() {
        return fom;
    }

    @JsonIgnore
    public LocalDateTime getTom() {
        return tom;
    }
}
