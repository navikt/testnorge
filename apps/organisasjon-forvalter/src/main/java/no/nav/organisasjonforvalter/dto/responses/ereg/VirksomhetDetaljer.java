package no.nav.organisasjonforvalter.dto.responses.ereg;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import no.nav.organisasjonforvalter.util.JavaTimeUtil;

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

    private LocalDate oppstartsdato;

    private LocalDate eierskiftedato;

    private LocalDate nedleggelsesdato;

    @Schema(description = "Enhetstype - virksomhet (kodeverk: EnhetstyperVirksomhet)", example = "BEDR")
    private String enhetstype;

    @Schema(description = "Er virksomheten ubemannet?", example = "false")
    private Boolean ubemannetVirksomhet;

    @JsonProperty("oppstartsdato")
    @Schema(description = "Dato for oppstart, format (ISO-8601): yyyy-MM-dd", example = "2014-07-15")
    public String getOppstartsdatoAsString() {
        return JavaTimeUtil.toString(oppstartsdato);
    }

    @JsonProperty("eierskiftedato")
    @Schema(description = "Dato for eierskifte, format (ISO-8601): yyyy-MM-dd", example = "2015-01-13")
    public String getEierskiftedatoAsString() {
        return JavaTimeUtil.toString(eierskiftedato);
    }

    @JsonProperty("nedleggelsesdato")
    @Schema(description = "Dato for nedleggelse, format (ISO-8601): yyyy-MM-dd", example = "2016-12-31")
    public String getNedleggelsesdatoAsString() {
        return JavaTimeUtil.toString(nedleggelsesdato);
    }
}
