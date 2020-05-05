package no.nav.dolly.bestilling.aareg.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArbeidsforholdResponse {

    public enum Aktoer {Organisasjon, Person}
    private List<Arbeidsforhold> arbeidsforhold;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Arbeidsforhold {
        private Ansettelsesperiode ansettelsesperiode;
        private List<AntallTimerForTimeloennet> antallTimerForTimeloennet;
        private List<Arbeidsavtale> arbeidsavtaler;
        private String arbeidsforholdId;
        private Arbeidsgiver arbeidsgiver;
        private Arbeidstaker arbeidstaker;
        private boolean innrapportertEtterAOrdningen;
        private Long navArbeidsforholdId;
        private Arbeidsgiver opplysningspliktig;
        private List<PermisjonPermittering> permisjonPermitteringer;
        private LocalDateTime registrert;
        private LocalDateTime sistBekreftet;
        private String type;
        private List<Utenlandsopphold> utenlandsopphold;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Utenlandsopphold {

        private String landkode;
        private Periode periode;
        private String rapporteringsperiode; // yyyy-mm
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PermisjonPermittering {

        private Periode periode;
        private String permisjonPermitteringId;
        private Double prosent;
        private String type;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Arbeidsgiver {

        private Aktoer type;
        private String organisasjonsnummer;
        private String offentligIdent;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Arbeidstaker {

        private Aktoer type;
        private String offentligIdent;
        private String aktoerId;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Arbeidsavtale {

        private Double antallTimerPrUke;
        private String arbeidstidsordning;
        private Double beregnetAntallTimerPrUke;
        private Periode bruksperiode;
        private Periode gyldighetsperiode;
        private String sistLoennsendring;
        private LocalDate sistStillingsendring;
        private Double stillingsprosent;
        private String yrke;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AntallTimerForTimeloennet {

        private Double antallTimer;
        private Periode periode;
        private String rapporteringsperiode; // yyyy-mm
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Ansettelsesperiode {

        private Periode bruksperiode;
        private Periode periode;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Periode {

        private LocalDateTime fom;
        private LocalDateTime tom;
    }
}


