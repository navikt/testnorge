package no.nav.dolly.bestilling.inntektsmelding.domain;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Inntektsmelding {

    private String miljoe;
    private String arbeidstakerFnr;
    private List<Inntektsmelding.RsInntektsmelding> inntekter;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RsInntektsmelding {

        private String aarsakTilInnsending;
        private RsArbeidsforhold arbeidsforhold;
        private RsArbeidsgiver arbeidsgiver;
        private String arbeidstakerFnr;
        private RsAvsendersystem avsendersystem;
        private List<RsNaturalYtelseDetaljer> gjenopptakelseNaturalytelseListe;
        private Boolean naerRelasjon;
        private List<RsOmsorgspenger> omsorgspenger;
        private List<RsNaturalYtelseDetaljer> opphoerAvNaturalytelseListe;
        private List<RsPeriode> pleiepengerPerioder;
        private RsRefusjon refusjon;
        private LocalDate startdatoForeldrepengeperiode;
        private RsSykepengerIArbeidsgiverperioden sykepengerIArbeidsgiverperioden;
        private String ytelse;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RsSykepengerIArbeidsgiverperioden {

        private List<RsPeriode> arbeidsgiverperiodeListe;
        private String begrunnelseForReduksjonEllerIkkeUtbetalt;
        private Double bruttoUtbetalt;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RsRefusjon {

        private List<RsEndringIRefusjon> endringIRefusjonListe;
        private Double refusjonsbeloepPrMnd;
        private LocalDate refusjonsopphoersdato;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RsEndringIRefusjon {

        private LocalDate endringsdato;
        private Double refusjonsbeloepPrMnd;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RsNaturalYtelseDetaljer {

        private Double beloepPrMnd;
        private LocalDate fom;
        private String naturaytelseType;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RsOmsorgspenger {

        private List<RsDelvisFravaer> delvisFravaersListe;
        private List<RsPeriode> fravaersPerioder;
        private Boolean harUtbetaltPliktigeDager;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RsDelvisFravaer {

        private LocalDate dato;
        private Double timer;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RsAvsendersystem {

        private LocalDate innsendingstidspunkt;
        private String systemnavn;
        private String systemversjon;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RsArbeidsgiver {

        private RsKontaktinformasjon kontaktinformasjon;
        private String virksomhetsnummer;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RsKontaktinformasjon {

        private String kontaktinformasjonNavn;
        private String telefonnummer;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RsArbeidsforhold {

        private String arbeidsforholdId;
        private List<RsPeriode> avtaltFerieListe;
        private RsAltinnInntekt beregnetInntekt;
        private LocalDate foersteFravaersdag;
        private List<RsGraderingIForeldrepenger> graderingIForeldrepengerListe;
        private List<RsUtsettelseAvForeldrepenger> utsettelseAvForeldrepengerListe;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RsUtsettelseAvForeldrepenger {

        private String aarsakTilUtsettelse;
        private RsPeriode periode;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RsGraderingIForeldrepenger {
        private Integer arbeidstidprosent;
        private RsPeriode periode;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RsAltinnInntekt {

        private String aarsakVedEndring;
        private Double beloep;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RsPeriode {

        private LocalDate fom;
        private LocalDate tom;
    }
}
