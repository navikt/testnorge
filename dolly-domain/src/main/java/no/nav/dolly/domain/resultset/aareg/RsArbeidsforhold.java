package no.nav.dolly.domain.resultset.aareg;

import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

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
public class RsArbeidsforhold {

    private String arbeidsforholdID;
    private Long arbeidsforholdIDnav;
    private RsPeriode ansettelsesPeriode;
    private String arbeidsforholdstype;
    private List<RsAntallTimerIPerioden> antallTimerForTimeloennet;
    private RsArbeidsavtale arbeidsavtale;
    private List<RsPermisjon> permisjon;
    private List<RsUtenlandsopphold> utenlandsopphold;
    private RsAktoer arbeidsgiver;
    private RsPerson arbeidstaker;

    public List<RsAntallTimerIPerioden> getAntallTimerForTimeloennet() {
        if (isNull(antallTimerForTimeloennet)) {
            antallTimerForTimeloennet = new ArrayList();
        }
        return antallTimerForTimeloennet;
    }

    public List<RsUtenlandsopphold> getUtenlandsopphold() {
        if (isNull(utenlandsopphold)) {
            utenlandsopphold = new ArrayList();
        }
        return utenlandsopphold;
    }

    public List<RsPermisjon> getPermisjon() {
        if (isNull(permisjon)) {
            permisjon = new ArrayList();
        }
        return permisjon;
    }
}
