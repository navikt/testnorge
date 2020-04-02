package no.nav.dolly.domain.resultset.inntektsmeldingstub;

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
public class RsInntektsmelding {

    private List<Inntektsmelding> inntekter;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Inntektsmelding {

        private String aarsakTilInnsending;
        private RsArbeidsforhold arbeidsforhold;
        private RsArbeidsgiver arbeidsgiver;
        private RsAvsendersystem avsendersystem;
        private List<RsNaturalYtelseDetaljer> gjenopptakelseNaturalytelseListe;
        private Boolean naerRelasjon;
        private List<RsOmsorgspenger> omsorgspenger;
        private List<RsNaturalYtelseDetaljer> opphoerAvNaturalytelseListe;
        private List<RsPeriode>         pleiepengerPerioder;
        private RsRefusjon refusjon;
        private LocalDateTime startdatoForeldrepengeperiode;
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
        private LocalDateTime refusjonsopphoersdato;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RsEndringIRefusjon {

        private LocalDateTime endringsdato;
        private Double refusjonsbeloepPrMnd;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RsNaturalYtelseDetaljer {

        private Double beloepPrMnd;
        private LocalDateTime fom;
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

        private LocalDateTime dato;
        private Double timer;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RsAvsendersystem {

        private LocalDateTime innsendingstidspunkt;
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
        private LocalDateTime foersteFravaersdag;
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

        private LocalDateTime fom;
        private LocalDateTime tom;
    }
}
