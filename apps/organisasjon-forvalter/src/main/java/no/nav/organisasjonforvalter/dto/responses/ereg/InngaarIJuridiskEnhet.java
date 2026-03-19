package no.nav.organisasjonforvalter.dto.responses.ereg;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(allOf = JuridiskEnhetNoekkelinfo.class)
@JsonPropertyOrder({
        "organisasjonsnummer",
        "navn",
        "bruksperiode",
        "gyldighetsperiode"
})
public class InngaarIJuridiskEnhet extends JuridiskEnhetNoekkelinfo {

    private Bruksperiode bruksperiode = new Bruksperiode();

    private Gyldighetsperiode gyldighetsperiode = new Gyldighetsperiode();
}
