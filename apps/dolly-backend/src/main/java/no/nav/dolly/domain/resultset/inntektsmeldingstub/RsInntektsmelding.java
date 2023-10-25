package no.nav.dolly.domain.resultset.inntektsmeldingstub;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

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
        @Field(type = FieldType.Date, format = DateFormat.basic_date, pattern = "uuuu-MM-dd")
        private LocalDate startdatoForeldrepengeperiode;
        private RsSykepengerIArbeidsgiverperioden sykepengerIArbeidsgiverperioden;
        private YtelseType ytelse;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RsSykepengerIArbeidsgiverperioden {

        private List<RsPeriode> arbeidsgiverperiodeListe;
        private BegrunnelseForReduksjonEllerIkkeUtbetaltType begrunnelseForReduksjonEllerIkkeUtbetalt;
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
        @Field(type = FieldType.Date, format = DateFormat.basic_date, pattern = "uuuu-MM-dd")
        private LocalDate refusjonsopphoersdato;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RsEndringIRefusjon {

        @Field(type = FieldType.Date, format = DateFormat.basic_date, pattern = "uuuu-MM-dd")
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
        @Field(type = FieldType.Date, format = DateFormat.basic_date, pattern = "uuuu-MM-dd")
        private LocalDate fom;
        private NaturalytelseType naturalytelseType;
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

        @Field(type = FieldType.Date, format = DateFormat.basic_date, pattern = "uuuu-MM-dd")
        private LocalDate dato;
        private Double timer;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RsAvsendersystem {

        @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, pattern = "uuuu-MM-dd'T'HH:mm:ss")
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
        @Field(type = FieldType.Date, format = DateFormat.basic_date, pattern = "uuuu-MM-dd")
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

        private AarsakTilUtsettelseType aarsakTilUtsettelse;
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

        private AarsakVedEndringType aarsakVedEndring;
        private Double beloep;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RsPeriode {

        @Field(type = FieldType.Date, format = DateFormat.basic_date, pattern = "uuuu-MM-dd")
        private LocalDate fom;
        @Field(type = FieldType.Date, format = DateFormat.basic_date, pattern = "uuuu-MM-dd")
        private LocalDate tom;
    }
}