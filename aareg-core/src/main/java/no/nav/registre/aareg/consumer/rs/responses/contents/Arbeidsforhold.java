package no.nav.registre.aareg.consumer.rs.responses.contents;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class Arbeidsforhold {

    @JsonProperty("ansettelsesPeriode")
    private Map<String, String> ansettelsesPeriode;

    @JsonProperty("antallTimerForTimeloennet")
    private List<AntallTimerForTimeloennet> antallTimerForTimeloennet;

    @JsonProperty("arbeidsavtale")
    private Arbeidsavtale arbeidsavtale;

    @JsonProperty("arbeidsforholdID")
    private String arbeidsforholdID;

    @JsonProperty("arbeidsforholdIDnav")
    private int arbeidsforholdIDnav;

    @JsonProperty("arbeidsforholdstype")
    private String arbeidsforholdstype;

    @JsonProperty("arbeidsgiver")
    private Map<String, String> arbeidsgiver;

    @JsonProperty("arbeidstaker")
    private Map<String, String> arbeidstaker;

    @JsonProperty("permisjon")
    private List<Permisjon> permisjon;

    @JsonProperty("utenlandsopphold")
    private List<Utenlandsopphold> utenlandsopphold;
}
