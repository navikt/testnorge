package no.nav.dolly.domain.resultset.sykemelding;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RsSykemelding {

    private RsDetaljertSykemelding detaljertSykemelding;
    private RsNySykemelding nySykemelding;

    @JsonIgnore
    public boolean hasDetaljertSykemelding() {

        return nonNull(detaljertSykemelding);
    }

    @JsonIgnore
    public boolean hasNySykemelding() {

        return nonNull(nySykemelding);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class RsNySykemelding {

        private List<Aktivitet> aktivitet;

        public List<Aktivitet> getAktivitet() {
            if (isNull(aktivitet)) {
                aktivitet = new ArrayList<>();
            }
            return aktivitet;
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public static class Aktivitet {

            private LocalDate fom;
            private LocalDate tom;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class RsDetaljertSykemelding {

        private Arbeidsgiver arbeidsgiver;
        private DollyDiagnose hovedDiagnose;
        private List<DollyDiagnose> biDiagnoser;
        private Detaljer detaljer;
        private Helsepersonell helsepersonell;
        private Boolean manglendeTilretteleggingPaaArbeidsplassen;
        private Organisasjon mottaker;
        private Pasient pasient;
        private List<Periode> perioder;
        private Organisasjon sender;
        private LocalDate startDato;
        private Boolean umiddelbarBistand;
        private KontaktMedPasient kontaktMedPasient;

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

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public static class Arbeidsgiver {

            private String navn;
            private Double stillingsprosent;
            private String yrkesbetegnelse;
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
        public static class DollyDiagnose {

            private String diagnose;
            private String diagnosekode;
            private String system;
        }

        @Data
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

        @Data
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

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public static class Organisasjon {

            private Adresse adresse;
            private String navn;
            private String orgNr;
        }

        @Data
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

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public static class Periode {

            private DollyAktivitet aktivitet;
            private LocalDate fom;
            private LocalDate tom;
        }

        @Data
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

        @Data
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

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public static class KontaktMedPasient {
            private LocalDate kontaktDato;
            private String begrunnelseIkkeKontakt;
        }
    }
}
