package no.nav.dolly.bestilling.inntektsmelding.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.enums.AarsakBeregnetInntektEndringKodeListe;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.enums.AarsakInnsendingKodeListe;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.enums.AarsakUtsettelseKodeListe;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.enums.BegrunnelseIngenEllerRedusertUtbetalingKodeListe;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.enums.NaturalytelseKodeListe;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InntektsmeldingRequest {

    public enum Avsendertype {ORGNR, FNR}

    private String miljoe;
    private String arbeidstakerFnr;
    private List<Inntektsmelding> inntekter;
    private JoarkMetadata joarkMetadata;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
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

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Inntektsmelding {

        private AarsakInnsendingKodeListe aarsakTilInnsending;
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

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SykepengerIArbeidsgiverperioden {

        private List<Periode> arbeidsgiverperiodeListe;
        private BegrunnelseIngenEllerRedusertUtbetalingKodeListe begrunnelseForReduksjonEllerIkkeUtbetalt;
        private Double bruttoUtbetalt;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Refusjon {

        private List<EndringIRefusjon> endringIRefusjonListe;
        private Double refusjonsbeloepPrMnd;
        private LocalDate refusjonsopphoersdato;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class EndringIRefusjon {

        private LocalDate endringsdato;
        private Double refusjonsbeloepPrMnd;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class NaturalYtelseDetaljer {

        private Double beloepPrMnd;
        private LocalDate fom;
        private NaturalytelseKodeListe naturalytelseType;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Omsorgspenger {

        private List<DelvisFravaer> delvisFravaersListe;
        private List<Periode> fravaersPerioder;
        private Boolean harUtbetaltPliktigeDager;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DelvisFravaer {

        private LocalDate dato;
        private Double timer;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Avsendersystem {

        private LocalDateTime innsendingstidspunkt;
        private String systemnavn;
        private String systemversjon;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Arbeidsgiver {

        private Kontaktinformasjon kontaktinformasjon;
        private String virksomhetsnummer;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ArbeidsgiverPrivat {

        private Kontaktinformasjon kontaktinformasjon;
        private String arbeidsgiverFnr;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Kontaktinformasjon {

        private String kontaktinformasjonNavn;
        private String telefonnummer;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Arbeidsforhold {

        private String arbeidsforholdId;
        private List<Periode> avtaltFerieListe;
        private AltinnInntekt beregnetInntekt;
        private LocalDate foersteFravaersdag;
        private List<GraderingIForeldrepenger> graderingIForeldrepengerListe;
        private List<UtsettelseAvForeldrepenger> utsettelseAvForeldrepengerListe;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class UtsettelseAvForeldrepenger {

        private AarsakUtsettelseKodeListe aarsakTilUtsettelse;
        private Periode periode;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class GraderingIForeldrepenger {
        private Integer arbeidstidprosent;
        private Periode periode;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AltinnInntekt {

        private AarsakBeregnetInntektEndringKodeListe aarsakVedEndring;
        private Double beloep;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Periode {

        private LocalDate fom;
        private LocalDate tom;
    }
}
