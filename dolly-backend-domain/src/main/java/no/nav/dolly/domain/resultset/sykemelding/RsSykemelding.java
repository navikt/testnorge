package no.nav.dolly.domain.resultset.sykemelding;

import static java.util.Objects.isNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class RsSykemelding {

    private RsSyntSykemelding syntSykemelding;
    private RsDetaljertSykemelding detaljertSykemelding;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    public static class RsSyntSykemelding {

        @ApiModelProperty(position = 1)
        private String arbeidsforholdId;

        @ApiModelProperty(position = 2)
        private String orgnummer;

        @ApiModelProperty(position = 3)
        private LocalDateTime startDato;
    }


    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    public static class RsDetaljertSykemelding {

        @ApiModelProperty(position = 1)
        private Arbeidsgiver arbeidsgiver;

        @ApiModelProperty(position = 2)
        private List<DollyDiagnose> biDiagnoser;

        @ApiModelProperty(position = 3)
        private Detaljer detaljer;

        @ApiModelProperty(position = 4)
        private DollyDiagnose hovedDiagnose;

        @ApiModelProperty(position = 5)
        private Helsepersonell helsepersonell;

        @ApiModelProperty(position = 6)
        private Boolean manglendeTilretteleggingPaaArbeidsplassen;

        @ApiModelProperty(position = 7)
        private Organisasjon mottaker;

        @ApiModelProperty(position = 8)
        private Pasient pasient;

        @ApiModelProperty(position = 9)
        private List<Periode> perioder;

        @ApiModelProperty(position = 10)
        private Organisasjon sender;

        @ApiModelProperty(position = 11)
        private LocalDate startDato;

        @ApiModelProperty(position = 12)
        private Boolean umiddelbarBistand;

        public List<DollyDiagnose> getBiDiagnoser() {
            if (isNull(biDiagnoser)) {
                biDiagnoser = new ArrayList<>();
            }
            return biDiagnoser;
        }

        public List<Periode> getPerioder() {
            if (isNull(perioder)) {
                perioder = new ArrayList<>();
            }
            return perioder;
        }

        public enum AktivitetType {

            INGEN, AVVENTENDE
        }

        @Getter
        @Setter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
        public static class Arbeidsgiver {

            @ApiModelProperty(position = 1)
            private String navn;

            @ApiModelProperty(position = 2)
            private Double stillingsprosent;

            @ApiModelProperty(position = 3)
            private String yrkesbetegnelse;
        }

        @Getter
        @Setter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
        public static class DollyDiagnose {

            @ApiModelProperty(position = 1)
            private String diagnose;

            @ApiModelProperty(position = 2)
            private String diagnosekode;

            @ApiModelProperty(position = 3)
            private String system;
        }

        @Getter
        @Setter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
        public static class Detaljer {

            @ApiModelProperty(position = 1)
            private Boolean arbeidsforEtterEndtPeriode;

            @ApiModelProperty(position = 2)
            private String beskrivHensynArbeidsplassen;

            @ApiModelProperty(position = 3)
            private String tiltakArbeidsplass;

            @ApiModelProperty(position = 4)
            private String tiltakNav;
        }

        @Getter
        @Setter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
        public static class Helsepersonell {

            @ApiModelProperty(position = 1)
            private String fornavn;

            @ApiModelProperty(position = 2)
            private String etternavn;

            @ApiModelProperty(position = 3)
            private String hprId;

            @ApiModelProperty(position = 4)
            private String ident;

            @ApiModelProperty(position = 5)
            private String mellomnavn;
        }

        @Getter
        @Setter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
        public static class Organisasjon {

            @ApiModelProperty(position = 1)
            private Adresse adresse;

            @ApiModelProperty(position = 2)
            private String navn;

            @ApiModelProperty(position = 3)
            private String orgNr;
        }

        @Getter
        @Setter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
        public static class Pasient {

            @ApiModelProperty(position = 1)
            private Adresse adresse;

            @ApiModelProperty(position = 2)
            private String etternavn;

            @ApiModelProperty(position = 3)
            private LocalDate foedselsdato;

            @ApiModelProperty(position = 4)
            private String fornavn;

            @ApiModelProperty(position = 5)
            private String ident;

            @ApiModelProperty(position = 6)
            private String mellomnavn;

            @ApiModelProperty(position = 7)
            private String navKontor;

            @ApiModelProperty(position = 8)
            private String telefon;
        }

        @Getter
        @Setter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
        public static class Periode {

            @ApiModelProperty(position = 1)
            private DollyAktivitet aktivitet;

            @ApiModelProperty(position = 2)
            private LocalDate fom;

            @ApiModelProperty(position = 3)
            private LocalDate tom;
        }

        @Getter
        @Setter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
        public static class DollyAktivitet {

            @ApiModelProperty(position = 1)
            private AktivitetType aktivitet;

            @ApiModelProperty(position = 2)
            private Integer behandlingsdager;

            @ApiModelProperty(position = 3)
            private Integer grad;

            @ApiModelProperty(position = 4)
            private Boolean reisetilskudd;

        }

        @Getter
        @Setter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
        public static class Adresse {

            @ApiModelProperty(position = 1)
            private String by;

            @ApiModelProperty(position = 2)
            private String gate;

            @ApiModelProperty(position = 3)
            private String land;

            @ApiModelProperty(position = 4)
            private String postnummer;
        }
    }

}
