package no.nav.registre.aareg.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class Arbeidsforhold {

    @JsonProperty("ansettelsesPeriode")
    private AnsettelsesPeriode ansettelsesPeriode;

    @JsonProperty("antallTimerForTimeloennet")
    private List<AntallTimerForTimeloennet> antallTimerForTimeloennet;

    @JsonProperty("arbeidsavtale")
    private Arbeidsavtale arbeidsavtale;

    @JsonProperty("arbeidsforholdID")
    private String arbeidsforholdID;

    @JsonProperty("arbeidsforholdIDnav")
    private Long arbeidsforholdIDnav;

    @JsonProperty("arbeidsforholdstype")
    private String arbeidsforholdstype;

    @JsonProperty("arbeidsgiver")
    private RsAktoer arbeidsgiver;

    @JsonProperty("arbeidstaker")
    private RsPersonAareg arbeidstaker;

    @JsonProperty("permisjon")
    private List<Permisjon> permisjon;

    @JsonProperty("utenlandsopphold")
    private List<Utenlandsopphold> utenlandsopphold;
}
