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
@Schema(description = "Informasjon om hjemlandregister")
@JsonPropertyOrder({
        "registernummer",
        "navn1",
        "navn2",
        "navn3",
        "postadresse",
        "bruksperiode",
        "gyldighetsperiode"
})
public class Hjemlandregister {

    private Bruksperiode bruksperiode = new Bruksperiode();

    private Gyldighetsperiode gyldighetsperiode = new Gyldighetsperiode();

    @Schema(description = "Registernummer", example = "0932568")
    private String registernummer;

    @Schema(description = "Navn #1")
    private String navn1;

    @Schema(description = "Navn #2")
    private String navn2;

    @Schema(description = "Navn #3")
    private String navn3;

    @Schema(description = "Postadresse for hjemlandregister")
    private Postadresse postadresse;
}
