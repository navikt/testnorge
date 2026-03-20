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
@Schema(description = "Informasjon om foretak i utland")
@JsonPropertyOrder({
        "landkode",
        "foretaksform",
        "beskrivelseHjemland",
        "beskrivelseNorge",
        "bruksperiode",
        "gyldighetsperiode"
})
public class UnderlagtHjemlandLovgivningForetaksform {

    private Bruksperiode bruksperiode = new Bruksperiode();

    private Gyldighetsperiode gyldighetsperiode = new Gyldighetsperiode();

    @Schema(description = "Landkode (kodeverk: Landkoder)", example = "GB")
    private String landkode;

    @Schema(description = "Foretaksform (kodeverk: ForetaksformUtland)", example = "LTD")
    private String foretaksform;

    @Schema(description = "Beskrivelse i hjemland")
    private String beskrivelseHjemland;

    @Schema(description = "Beskrivelse i Norge")
    private String beskrivelseNorge;
}
