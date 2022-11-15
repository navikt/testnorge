package no.nav.testnav.apps.syntaaregservice.domain.aareg;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
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
