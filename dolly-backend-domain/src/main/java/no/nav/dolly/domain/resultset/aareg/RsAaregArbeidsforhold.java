package no.nav.dolly.domain.resultset.aareg;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class RsAaregArbeidsforhold {

    @Schema(required = true)
    private RsPeriode ansettelsesPeriode;

    @Schema(required = true,
            description = "Gyldige verdier finnes i kodeverk 'Arbeidsforholdstyper'")
    private String arbeidsforholdstype;

    private List<RsAntallTimerIPerioden> antallTimerForTimeloennet;

    @Schema(required = true)
    private RsArbeidsavtale arbeidsavtale;

    private List<RsPermisjon> permisjon;

    private List<RsUtenlandsopphold> utenlandsopphold;

    @Schema(required = true)
    private RsAktoer arbeidsgiver;
}
