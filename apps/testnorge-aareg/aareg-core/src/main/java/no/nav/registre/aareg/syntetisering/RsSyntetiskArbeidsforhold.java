package no.nav.registre.aareg.syntetisering;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import no.nav.registre.aareg.domain.RsAktoer;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsSyntetiskArbeidsforhold {

    private RsSyntetiskPeriode ansettelsesPeriode;
    private List<RsSyntetiskAntallTimerForTimeloennet> antallTimerForTimeloennet;
    private RsSyntetiskArbeidsavtale arbeidsavtale;
    private String arbeidsforholdID;
    private Long arbeidsforholdIDnav;
    private String arbeidsforholdstype;
    private RsAktoer arbeidsgiver;
    private RsSyntPerson arbeidstaker;
    private List<RsSyntetiskPermisjon> permisjon;
    private List<RsSyntetiskUtenlandsopphold> utenlandsopphold;
}
