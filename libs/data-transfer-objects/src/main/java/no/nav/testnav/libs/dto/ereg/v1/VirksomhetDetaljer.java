package no.nav.testnav.libs.dto.ereg.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "enhetstype",
        "ubemannetVirksomhet",
        "oppstartsdato",
        "eierskiftedato",
        "nedleggelsesdato"
})
public class VirksomhetDetaljer {

    @Schema(description = "Dato for oppstart, format (ISO-8601): yyyy-MM-dd", example = "2014-07-15")
    private LocalDate oppstartsdato;

    @Schema(description = "Dato for eierskifte, format (ISO-8601): yyyy-MM-dd", example = "2015-01-13")
    private LocalDate eierskiftedato;

    @Schema(description = "Dato for nedleggelse, format (ISO-8601): yyyy-MM-dd", example = "2016-12-31")
    private LocalDate nedleggelsesdato;

    @Schema(description = "Enhetstype - virksomhet (kodeverk: EnhetstyperVirksomhet)", example = "BEDR")
    private String enhetstype;

    @Schema(description = "Er virksomheten ubemannet?", example = "false")
    private Boolean ubemannetVirksomhet;
}
