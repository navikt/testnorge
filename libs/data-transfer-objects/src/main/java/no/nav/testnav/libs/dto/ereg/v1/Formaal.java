package no.nav.testnav.libs.dto.ereg.v1;

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
@Schema(description = "Informasjon om form&aring;l")
@JsonPropertyOrder({
        "formaal",
        "bruksperiode",
        "gyldighetsperiode"
})
@SuppressWarnings("squid:S1700")
public class Formaal {

    private Bruksperiode bruksperiode = new Bruksperiode();

    private Gyldighetsperiode gyldighetsperiode = new Gyldighetsperiode();

    @Schema(description = "Form&aring;l", example = "Veivedlikehold, vaktmestertjenester, transport.")
    private String formaal;
}
