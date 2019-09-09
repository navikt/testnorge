package no.nav.registre.syntrest.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AaregResponse {

    Arbeidsforhold arbeidsforhold;

    @Getter @Setter @AllArgsConstructor
    private class Arbeidsforhold {
        AnsettelsesPeriode ansettelsesPeriode;
        Arbeidsavtale arbeidsavtale;
        String arbeidsforholdID;
        String arbeidsforholdstype;
        Arbeidsgiver arbeidsgiver;
        Arbeidstaker arbeidstaker;
    }
    @Getter @Setter @AllArgsConstructor
    private static class AnsettelsesPeriode {
        String fom;
    }
    @Getter @Setter @AllArgsConstructor
    private static class Arbeidsavtale {
        String arbeidstidsordning;
        double avtaltArbeidstimerPerUke;
        double stillingsprosent;
        String endringsdatoStillingsprosent;
        String yrke;
    }
    @Getter @Setter @AllArgsConstructor
    private static class Arbeidsgiver {
        String aktoertype;
        String orgnummer;
    }
    @Getter @Setter @AllArgsConstructor
    private static class Arbeidstaker {
        String aktoertype;
        String ident;
        String identtype;
    }
}