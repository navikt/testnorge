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
@Schema(description = "Informasjon om merverdiavgift")
@JsonPropertyOrder({
        "registrertIMVA",
        "bruksperiode",
        "gyldighetsperiode"
})
public class MVA {

    private Bruksperiode bruksperiode = new Bruksperiode();

    private Gyldighetsperiode gyldighetsperiode = new Gyldighetsperiode();

    @Schema(description = "Registrert i Merverdiregisteret?", example = "true")
    private Boolean registrertIMVA;
}
