package no.nav.dolly.domain.resultset.inntektsmeldingstub;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsInntektsmelding {

    private List<Inntektsmelding> inntekter;
    private JoarkMetadata joarkMetadata;

    public List<Inntektsmelding> getInntekter() {

        if (isNull(inntekter)) {
            inntekter = new ArrayList<>();
        }
        return inntekter;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JoarkMetadata {

        private String avsenderMottakerIdType;
        private String brevkategori;
        private String brevkode;
        private String brukerIdType;
        private String eksternReferanseId;
        private String filtypeArkiv;
        private String filtypeOriginal;
        private String journalpostType;
        private String kanal;
        private String tema;
        private String tittel;
        private String variantformatArkiv;
        private String variantformatOriginal;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Inntektsmelding {

        private AarsakTilInnsendingType aarsakTilInnsending;
        private RsArbeidsforhold arbeidsforhold;
        private RsArbeidsgiver arbeidsgiver;
        private RsArbeidsgiverPrivat arbeidsgiverPrivat;
        private RsAvsendersystem avsendersystem;
        private List<RsNaturalYtelseDetaljer> gjenopptakelseNaturalytelseListe;
        private Boolean naerRelasjon;
        private RsOmsorgspenger omsorgspenger;
        private List<RsNaturalYtelseDetaljer> opphoerAvNaturalytelseListe;
        private List<RsPeriode> pleiepengerPerioder;
        private RsRefusjon refusjon;
        private LocalDate startdatoForeldrepengeperiode;
        private RsSykepengerIArbeidsgiverperioden sykepengerIArbeidsgiverperioden;
        private YtelseType ytelse;

        public List<RsPeriode> getPleiepengerPerioder() {

            if (isNull(pleiepengerPerioder)) {
                pleiepengerPerioder = new ArrayList<>();
            }
            return pleiepengerPerioder;
        }

        public List<RsNaturalYtelseDetaljer> getGjenopptakelseNaturalytelseListe() {

            if (isNull(gjenopptakelseNaturalytelseListe)) {
                gjenopptakelseNaturalytelseListe = new ArrayList<>();
            }
            return gjenopptakelseNaturalytelseListe;
        }

        public List<RsNaturalYtelseDetaljer> getOpphoerAvNaturalytelseListe() {

            if (isNull(opphoerAvNaturalytelseListe)) {
                opphoerAvNaturalytelseListe = new ArrayList<>();
            }
            return opphoerAvNaturalytelseListe;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RsSykepengerIArbeidsgiverperioden {

        private List<RsPeriode> arbeidsgiverperiodeListe;
        private BegrunnelseForReduksjonEllerIkkeUtbetaltType begrunnelseForReduksjonEllerIkkeUtbetalt;
        private Double bruttoUtbetalt;

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RsRefusjon {

        private List<RsEndringIRefusjon> endringIRefusjonListe;
        private Double refusjonsbeloepPrMnd;
        private LocalDate refusjonsopphoersdato;

        public List<RsEndringIRefusjon> getEndringIRefusjonListe() {

            if (isNull(endringIRefusjonListe)) {
                endringIRefusjonListe = new ArrayList<>();
            }
            return endringIRefusjonListe;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RsEndringIRefusjon {

        private LocalDate endringsdato;
        private Double refusjonsbeloepPrMnd;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RsNaturalYtelseDetaljer {

        private Double beloepPrMnd;
        private LocalDate fom;
        private NaturalytelseType naturalytelseType;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RsOmsorgspenger {

        private List<RsDelvisFravaer> delvisFravaersListe;
        private List<RsPeriode> fravaersPerioder;
        private Boolean harUtbetaltPliktigeDager;

        public List<RsDelvisFravaer> getDelvisFravaersListe() {

            if (isNull(delvisFravaersListe)) {
                delvisFravaersListe = new ArrayList<>();
            }
            return delvisFravaersListe;
        }

        public List<RsPeriode> getFravaersPerioder() {

            if (isNull(fravaersPerioder)) {
                fravaersPerioder = new ArrayList<>();
            }
            return fravaersPerioder;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RsDelvisFravaer {

        private LocalDate dato;
        private Double timer;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RsAvsendersystem {

        private LocalDateTime innsendingstidspunkt;
        private String systemnavn;
        private String systemversjon;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RsArbeidsgiver {

        private RsKontaktinformasjon kontaktinformasjon;
        private String virksomhetsnummer;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RsArbeidsgiverPrivat {

        private RsKontaktinformasjon kontaktinformasjon;
        private String arbeidsgiverFnr;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RsKontaktinformasjon {

        private String kontaktinformasjonNavn;
        private String telefonnummer;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RsArbeidsforhold {

        private String arbeidsforholdId;
        private List<RsPeriode> avtaltFerieListe;
        private RsAltinnInntekt beregnetInntekt;
        private LocalDate foersteFravaersdag;
        private List<RsGraderingIForeldrepenger> graderingIForeldrepengerListe;
        private List<RsUtsettelseAvForeldrepenger> utsettelseAvForeldrepengerListe;

        public List<RsPeriode> getAvtaltFerieListe() {

            if (isNull(avtaltFerieListe)) {
                avtaltFerieListe = new ArrayList<>();
            }
            return avtaltFerieListe;
        }

        public List<RsGraderingIForeldrepenger> getGraderingIForeldrepengerListe() {

            if (isNull(graderingIForeldrepengerListe)) {
                graderingIForeldrepengerListe = new ArrayList<>();
            }
            return graderingIForeldrepengerListe;
        }

        public List<RsUtsettelseAvForeldrepenger> getUtsettelseAvForeldrepengerListe() {

            if (isNull(utsettelseAvForeldrepengerListe)) {
                utsettelseAvForeldrepengerListe = new ArrayList<>();
            }
            return utsettelseAvForeldrepengerListe;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RsUtsettelseAvForeldrepenger {

        private AarsakTilUtsettelseType aarsakTilUtsettelse;
        private RsPeriode periode;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RsGraderingIForeldrepenger {
        private Integer arbeidstidprosent;
        private RsPeriode periode;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RsAltinnInntekt {

        private AarsakVedEndringType aarsakVedEndring;
        private Double beloep;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RsPeriode {

        private LocalDate fom;
        private LocalDate tom;
    }
}