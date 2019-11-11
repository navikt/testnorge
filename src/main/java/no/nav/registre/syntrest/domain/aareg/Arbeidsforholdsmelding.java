package no.nav.registre.syntrest.domain.aareg;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Arbeidsforholdsmelding {

    @JsonProperty
    Arbeidsforhold arbeidsforhold;

    @Getter @Setter @AllArgsConstructor
    private class Arbeidsforhold {
        @JsonProperty
        AnsettelsesPeriode ansettelsesPeriode;
        @JsonProperty
        Arbeidsavtale arbeidsavtale;
        @JsonProperty
        String arbeidsforholdID;
        @JsonProperty
        String arbeidsforholdstype;
        @JsonProperty
        Arbeidsgiver arbeidsgiver;
        @JsonProperty
        Arbeidstaker arbeidstaker;
    }
    @Getter @Setter @AllArgsConstructor
    private static class AnsettelsesPeriode {
        @JsonProperty
        String fom;
    }
    @Getter @Setter @AllArgsConstructor
    private static class Arbeidsavtale {
        @JsonProperty
        String arbeidstidsordning;
        @JsonProperty
        double avtaltArbeidstimerPerUke;
        @JsonProperty
        double stillingsprosent;
        @JsonProperty
        String endringsdatoStillingsprosent;
        @JsonProperty
        String yrke;
    }
    @Getter @Setter @AllArgsConstructor
    private static class Arbeidsgiver {
        @JsonProperty
        String aktoertype;
        @JsonProperty
        String orgnummer;
    }
    @Getter @Setter @AllArgsConstructor
    private static class Arbeidstaker {
        @JsonProperty
        String aktoertype;
        @JsonProperty
        String ident;
        @JsonProperty
        String identtype;
    }
}