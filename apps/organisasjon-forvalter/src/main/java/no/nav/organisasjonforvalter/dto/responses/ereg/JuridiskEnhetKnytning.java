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
@JsonPropertyOrder({
        "knytning",
        "juridiskEnhet",
        "bruksperiode",
        "gyldighetsperiode"
})
public class JuridiskEnhetKnytning {

    private Bruksperiode bruksperiode = new Bruksperiode();

    private Gyldighetsperiode gyldighetsperiode = new Gyldighetsperiode();

    @Schema(description = "Knytning - juridisk enhet (kodeverk: Knytninger)", example = "KIRK")
    private String knytning;

    @Schema(description = "Informasjon om juridisk enhet")
    private JuridiskEnhet juridiskEnhet;
}
