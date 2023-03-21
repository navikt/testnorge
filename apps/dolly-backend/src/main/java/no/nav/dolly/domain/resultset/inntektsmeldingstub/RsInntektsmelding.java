package no.nav.dolly.domain.resultset.inntektsmeldingstub;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.enums.AarsakBeregnetInntektEndringKoder;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.enums.AarsakInnsendingKoder;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.enums.AarsakUtsettelseKoder;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.enums.BegrunnelseIngenEllerRedusertUtbetalingKoder;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.enums.NaturalytelseKoder;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.enums.YtelseKoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsInntektsmelding {

    private List<Inntektsmelding> inntekter;
    private JoarkMetadata joarkMetadata;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
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

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Inntektsmelding {

        private AarsakInnsendingKoder aarsakTilInnsending;
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
        private YtelseKoder ytelse;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RsSykepengerIArbeidsgiverperioden {

        private List<RsPeriode> arbeidsgiverperiodeListe;
        private BegrunnelseIngenEllerRedusertUtbetalingKoder begrunnelseForReduksjonEllerIkkeUtbetalt;
        private Double bruttoUtbetalt;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RsRefusjon {

        private List<RsEndringIRefusjon> endringIRefusjonListe;
        private Double refusjonsbeloepPrMnd;
        private LocalDate refusjonsopphoersdato;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RsEndringIRefusjon {

        private LocalDate endringsdato;
        private Double refusjonsbeloepPrMnd;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RsNaturalYtelseDetaljer {

        private Double beloepPrMnd;
        private LocalDate fom;
        private NaturalytelseKoder naturalytelseType;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RsOmsorgspenger {

        private List<RsDelvisFravaer> delvisFravaersListe;
        private List<RsPeriode> fravaersPerioder;
        private Boolean harUtbetaltPliktigeDager;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RsDelvisFravaer {

        private LocalDate dato;
        private Double timer;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RsAvsendersystem {

        private LocalDateTime innsendingstidspunkt;
        private String systemnavn;
        private String systemversjon;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RsArbeidsgiver {

        private RsKontaktinformasjon kontaktinformasjon;
        private String virksomhetsnummer;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RsArbeidsgiverPrivat {

        private RsKontaktinformasjon kontaktinformasjon;
        private String arbeidsgiverFnr;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RsKontaktinformasjon {

        private String kontaktinformasjonNavn;
        private String telefonnummer;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
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
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RsUtsettelseAvForeldrepenger {

        private AarsakUtsettelseKoder aarsakTilUtsettelse;
        private RsPeriode periode;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RsGraderingIForeldrepenger {
        private Integer arbeidstidprosent;
        private RsPeriode periode;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RsAltinnInntekt {

        private AarsakBeregnetInntektEndringKoder aarsakVedEndring;
        private Double beloep;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RsPeriode {

        private LocalDate fom;
        private LocalDate tom;
    }
}