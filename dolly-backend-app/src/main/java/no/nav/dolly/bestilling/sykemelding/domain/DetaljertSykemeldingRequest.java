package no.nav.dolly.bestilling.sykemelding.domain;

import static java.util.Objects.isNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

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
public class DetaljertSykemeldingRequest {

    private Arbeidsgiver arbeidsgiver;
    private List<Diagnose> biDiagnoser;
    private Detaljer detaljer;
    private Diagnose hovedDiagnose;
    private Lege lege;
    private Boolean manglendeTilretteleggingPaaArbeidsplassen;
    private Organisasjon mottaker;
    private Pasient pasient;
    private List<Periode> perioder;
    private Organisasjon sender;
    private LocalDateTime startDato;
    private Boolean umiddelbarBistand;

    public List<Diagnose> getBiDiagnoser() {
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
    public static class Diagnose {

        private String diagnose;
        private String diagnosekode;
        private String system;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
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
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private static class Lege {

        private String fornavn;
        private String etternavn;
        private String hprId;
        private String ident;
        private String mellomnavn;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private static class Organisasjon {

        private Adresse adresse;
        private String navn;
        private String orgNr;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private static class Pasient {

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
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private static class Periode {

        private Aktivitet aktivitet;
        private LocalDate fom;
        private LocalDate tom;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private static class Aktivitet {

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
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private static class Adresse {

        private String by;
        private String gate;
        private String land;
        private String postnummer;
    }
}
