package no.nav.organisasjonforvalter.dto.responses.ereg;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "kapital",
        "kapitalInnbetalt",
        "kapitalBundetKs",
        "valuta",
        "fritekst",
        "bruksperiode",
        "gyldighetsperiode"
})
public class Kapitalopplysninger {

    private Bruksperiode bruksperiode = new Bruksperiode();

    private Gyldighetsperiode gyldighetsperiode = new Gyldighetsperiode();

    @Schema(description = "Valuta (kodeverk: Valutaer)", example = "NOK")
    private String valuta;

    @Schema(description = "Kapital", example = "714587398")
    private BigDecimal kapital;

    @Schema(description = "Innbetalt kapital", example = "531710600")
    private BigDecimal kapitalInnbetalt;

    @Schema(description = "Bundet kapital", example = "285834959.20")
    private String kapitalBundetKs;

    @Schema(description = "Fritekst for informasjon om kapital")
    private String fritekst;
}
