package no.nav.testnav.libs.dto.aareg.v1;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "" +
        "Resultatobjekt for finn-arbeidsforhold-per-arbeidsgiver\n" +
        "En tom liste og antall > 0 betyr at s&oslash;ket gir for mange treff\n" +
        "Arbeidsforhold er filtrert grunnet tilgangskontroll hvis st&oslash;rrelse p&aring; liste er mindre enn (total) antall"
)
public class ArbeidsgiverArbeidsforhold {

    @Schema(description = "Liste av arbeidsforhold")
    private List<Arbeidsforhold> arbeidsforhold;

    @Schema(description = "Totalt antall arbeidsforhold")
    private int antall;
}
