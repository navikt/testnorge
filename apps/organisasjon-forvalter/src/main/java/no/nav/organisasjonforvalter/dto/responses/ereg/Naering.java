package no.nav.organisasjonforvalter.dto.responses.ereg;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(description = "Informasjon om n&aelig;ring")
@JsonPropertyOrder({
        "naeringskode",
        "hjelpeenhet",
        "bruksperiode",
        "gyldighetsperiode"
})
public class Naering {

    private Bruksperiode bruksperiode = new Bruksperiode();

    private Gyldighetsperiode gyldighetsperiode = new Gyldighetsperiode();

    @Schema(description = "N&aelig;ringskode (kodeverk: N_c3_a6ringskoder)", example = "62.030")
    private String naeringskode;

    @Schema(description = "Hjelpeenhet?", example = "false")
    private Boolean hjelpeenhet;
}
