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
        "Resultatobjekt for finn-arbeidsforholdoversikter-per-arbeidsgiver\n" +
        "Arbeidsforholdoversikter er filtrert grunnet tilgangskontroll hvis størrelse på liste er mindre enn (total) antall (forutsatt at antall- og/eller startrad-filter ikke er angitt)"
)
public class ArbeidsgiverArbeidsforholdoversikter {

    @Schema(description = "Liste av arbeidsforholdoversikter")
    private List<Arbeidsforholdoversikt> arbeidsforholdoversikter;

    @Schema(description = "Nummer for f&oslash;rste rad i resultatsett (ikke angitt hvis antall er 0)")
    private Integer startrad;

    @Schema(description = "Antall arbeidsforholdoversikter i resultatsett - der det er siste resultatsett hvis antall er mindre enn forespurt antall")
    private Integer antall;

    @Schema(description = "Total antall arbeidsforholdoversikter")
    private Integer totalAntall;
}
