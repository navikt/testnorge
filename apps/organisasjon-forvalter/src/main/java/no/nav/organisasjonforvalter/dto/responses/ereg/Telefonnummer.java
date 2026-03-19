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
@Schema(description = "Informasjon om telefonnummer")
@JsonPropertyOrder({
        "nummer",
        "telefontype",
        "bruksperiode",
        "gyldighetsperiode"
})
public class Telefonnummer {

    private Bruksperiode bruksperiode = new Bruksperiode();

    private Gyldighetsperiode gyldighetsperiode = new Gyldighetsperiode();

    @Schema(description = "Telefonnummer", example = "21 07 00 00")
    private String nummer;

    @Schema(description = "Telefontype (kodeverk: Telefontyper)", example = "TFON")
    private String telefontype;
}
