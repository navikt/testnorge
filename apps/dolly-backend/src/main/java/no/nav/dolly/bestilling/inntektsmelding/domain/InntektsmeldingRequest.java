package no.nav.dolly.bestilling.inntektsmelding.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.resultset.inntektsmeldingstub.AarsakTilInnsendingType;
import no.nav.dolly.domain.resultset.inntektsmeldingstub.AarsakTilUtsettelseType;
import no.nav.dolly.domain.resultset.inntektsmeldingstub.AarsakVedEndringType;
import no.nav.dolly.domain.resultset.inntektsmeldingstub.BegrunnelseForReduksjonEllerIkkeUtbetaltType;
import no.nav.dolly.domain.resultset.inntektsmeldingstub.NaturalytelseType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InntektsmeldingRequest {

    public enum Avsendertype {ORGNR, FNR}

    private String miljoe;
    private String arbeidstakerFnr;
    private List<Inntektsmelding> inntekter;
    private JoarkMetadata joarkMetadata;

    @Data
    @NoArgsConstructor
    public static class JoarkMetadata {

        private Avsendertype avsenderMottakerIdType;
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
    @NoArgsConstructor
    public static class Inntektsmelding {

        private AarsakTilInnsendingType aarsakTilInnsending;
        private Arbeidsforhold arbeidsforhold;
        private Arbeidsgiver arbeidsgiver;
        private ArbeidsgiverPrivat arbeidsgiverPrivat;
        private Avsendersystem avsendersystem;
        private List<NaturalYtelseDetaljer> gjenopptakelseNaturalytelseListe;
        private Boolean naerRelasjon;
        private Omsorgspenger omsorgspenger;
        private List<NaturalYtelseDetaljer> opphoerAvNaturalytelseListe;
        private List<Periode> pleiepengerPerioder;
        private Refusjon refusjon;
        private LocalDate startdatoForeldrepengeperiode;
        private SykepengerIArbeidsgiverperioden sykepengerIArbeidsgiverperioden;
        private String ytelse;
    }

    @Data
    @NoArgsConstructor
    public static class SykepengerIArbeidsgiverperioden {

        private List<Periode> arbeidsgiverperiodeListe;
        private BegrunnelseForReduksjonEllerIkkeUtbetaltType begrunnelseForReduksjonEllerIkkeUtbetalt;
        private Double bruttoUtbetalt;
    }

    @Data
    @NoArgsConstructor
    public static class Refusjon {

        private List<EndringIRefusjon> endringIRefusjonListe;
        private Double refusjonsbeloepPrMnd;
        private LocalDate refusjonsopphoersdato;
    }

    @Data
    @NoArgsConstructor
    public static class EndringIRefusjon {

        private LocalDate endringsdato;
        private Double refusjonsbeloepPrMnd;
    }

    @Data
    @NoArgsConstructor
    public static class NaturalYtelseDetaljer {

        private Double beloepPrMnd;
        private LocalDate fom;
        private NaturalytelseType naturalytelseType;
    }

    @Data
    @NoArgsConstructor
    public static class Omsorgspenger {

        private List<DelvisFravaer> delvisFravaersListe;
        private List<Periode> fravaersPerioder;
        private Boolean harUtbetaltPliktigeDager;
    }

    @Data
    @NoArgsConstructor
    public static class DelvisFravaer {

        private LocalDate dato;
        private Double timer;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Avsendersystem {

        private LocalDateTime innsendingstidspunkt;
        private String systemnavn;
        private String systemversjon;
    }

    @Data
    @NoArgsConstructor
    public static class Arbeidsgiver {

        private Kontaktinformasjon kontaktinformasjon;
        private String virksomhetsnummer;
    }

    @Data
    @NoArgsConstructor
    public static class ArbeidsgiverPrivat {

        private Kontaktinformasjon kontaktinformasjon;
        private String arbeidsgiverFnr;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Kontaktinformasjon {

        private String kontaktinformasjonNavn;
        private String telefonnummer;
    }

    @Data
    @NoArgsConstructor
    public static class Arbeidsforhold {

        private String arbeidsforholdId;
        private List<Periode> avtaltFerieListe;
        private AltinnInntekt beregnetInntekt;
        private LocalDate foersteFravaersdag;
        private List<GraderingIForeldrepenger> graderingIForeldrepengerListe;
        private List<UtsettelseAvForeldrepenger> utsettelseAvForeldrepengerListe;
    }

    @Data
    @NoArgsConstructor
    public static class UtsettelseAvForeldrepenger {

        private AarsakTilUtsettelseType aarsakTilUtsettelse;
        private Periode periode;
    }

    @Data
    @NoArgsConstructor
    public static class GraderingIForeldrepenger {
        private Integer arbeidstidprosent;
        private Periode periode;
    }

    @Data
    @NoArgsConstructor
    public static class AltinnInntekt {

        private AarsakVedEndringType aarsakVedEndring;
        private Double beloep;
    }

    @Data
    @NoArgsConstructor
    public static class Periode {

        private LocalDate fom;
        private LocalDate tom;
    }
}
