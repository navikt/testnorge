package no.nav.registre.aareg.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RsArbeidsforhold {

    private RsPeriode ansettelsesPeriode;
    private List<RsAntallTimerForTimeloennet> antallTimerForTimeloennet;
    private RsArbeidsavtale arbeidsavtale;
    private String arbeidsforholdID;
    private Long arbeidsforholdIDnav;
    private String arbeidsforholdstype;
    private RsAktoer arbeidsgiver;
    private RsPersonAareg arbeidstaker;
    private List<RsPermisjon> permisjon;
    private List<RsUtenlandsopphold> utenlandsopphold;
}
