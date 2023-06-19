package no.nav.dolly.domain.resultset.sykemelding;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RsSykemelding {

    private RsSyntSykemelding syntSykemelding;
    private RsDetaljertSykemelding detaljertSykemelding;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class RsSyntSykemelding {

        private String arbeidsforholdId;
        private String orgnummer;
        private LocalDateTime startDato;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class RsDetaljertSykemelding {

        private Arbeidsgiver arbeidsgiver;
        private List<DollyDiagnose> biDiagnoser;
        private Detaljer detaljer;
        private DollyDiagnose hovedDiagnose;
        private Helsepersonell helsepersonell;
        private Boolean manglendeTilretteleggingPaaArbeidsplassen;
        private Organisasjon mottaker;
        private Pasient pasient;
        private List<Periode> perioder;
        private Organisasjon sender;
        private LocalDate startDato;
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
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public static class Arbeidsgiver {

            private String navn;
            private Double stillingsprosent;
            private String yrkesbetegnelse;
        }

        @Getter
        @Setter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
        public static class DollyDiagnose {

            private String diagnose;
            private String diagnosekode;
            private String system;
        }

        @Getter
        @Setter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public static class Detaljer {

            private Boolean arbeidsforEtterEndtPeriode;
            private String beskrivHensynArbeidsplassen;
            private String tiltakArbeidsplass;
            private String tiltakNav;
        }

        @Getter
        @Setter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public static class Helsepersonell {

            private String fornavn;
            private String etternavn;
            private String hprId;
            private String ident;
            private String mellomnavn;
            private String samhandlerType;
        }

        @Getter
        @Setter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public static class Organisasjon {

            private Adresse adresse;
            private String navn;
            private String orgNr;
        }

        @Getter
        @Setter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public static class Pasient {

            private Adresse adresse;
            private String etternavn;
            private LocalDate foedselsdato;
            private String fornavn;
            private String ident;
            private String mellomnavn;
            private String navKontor;
            private String telefon;
        }

        @Getter
        @Setter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public static class Periode {

            private DollyAktivitet aktivitet;
            private LocalDate fom;
            private LocalDate tom;
        }

        @Getter
        @Setter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public static class DollyAktivitet {

            private AktivitetType aktivitet;
            private Integer behandlingsdager;
            private Integer grad;
            private Boolean reisetilskudd;

        }

        @Getter
        @Setter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public static class Adresse {

            private String by;
            private String gate;
            private String land;
            private String postnummer;
        }
    }
}
