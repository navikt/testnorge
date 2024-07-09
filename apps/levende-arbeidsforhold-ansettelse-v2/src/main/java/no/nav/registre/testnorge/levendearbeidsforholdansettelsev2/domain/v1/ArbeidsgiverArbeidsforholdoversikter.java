package no.nav.registre.testnorge.levendearbeidsforholdansettelsev2.domain.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "arbeidsforholdoversikter",
        "startrad",
        "antall",
        "totalAntall"
})
@Schema(description = """
        Resultatobjekt for finn-arbeidsforholdoversikter-per-arbeidsgiver

        Arbeidsforholdoversikter er filtrert grunnet tilgangskontroll hvis st&oslash;rrelse p&aring; liste er mindre enn (total) antall (forutsatt at antall- og/eller startrad-filter ikke er angitt)"""
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
