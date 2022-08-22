package no.nav.dolly.domain.resultset.aareg;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RsArbeidsforholdAareg {

    private String arbeidsforholdID;

    private Long arbeidsforholdIDnav;

    private RsAnsettelsesPeriode ansettelsesPeriode;

    private String arbeidsforholdstype;

    private List<RsAntallTimerIPerioden> antallTimerForTimeloennet;

    private List<RsFartoy> fartoy;

    private RsArbeidsavtale arbeidsavtale;

    private List<RsPermisjon> permisjon;

    private List<RsPermittering> permittering;

    private List<RsUtenlandsopphold> utenlandsopphold;

    private RsArbeidsgiver arbeidsgiver;

    private RsPersonAareg arbeidstaker;

    public List<RsAntallTimerIPerioden> getAntallTimerForTimeloennet() {
        if (isNull(antallTimerForTimeloennet)) {
            antallTimerForTimeloennet = new ArrayList<>();
        }
        return antallTimerForTimeloennet;
    }

    public List<RsUtenlandsopphold> getUtenlandsopphold() {
        if (isNull(utenlandsopphold)) {
            utenlandsopphold = new ArrayList<>();
        }
        return utenlandsopphold;
    }

    public List<RsPermisjon> getPermisjon() {
        if (isNull(permisjon)) {
            permisjon = new ArrayList<>();
        }
        return permisjon;
    }

    public List<RsPermittering> getPermittering() {
        if (isNull(permittering)) {
            permittering = new ArrayList<>();
        }
        return permittering;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RsArbeidsgiver {
        private String aktoertype;
        private String orgnummer;
    }
}
