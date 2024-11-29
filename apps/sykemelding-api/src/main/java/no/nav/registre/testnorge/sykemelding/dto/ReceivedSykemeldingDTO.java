package no.nav.registre.testnorge.sykemelding.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceivedSykemeldingDTO {

    private Sykemelding sykmelding;

    private String personNrPasient;
    private String tlfPasient;
    private String personNrLege;
    private String legeHelsepersonellkategori;
    private String legeHprNr;
    private String navLogId;
    private String msgId;
    private String legekontorOrgNr;
    private String legekontorHerId;
    private String legekontorReshId;
    private String legekontorOrgName;
    private LocalDateTime mottattDato;
    private String rulesetVersion;
    private List<Merknad> merknader;
    private String partnerreferanse;
    private List<String> vedlegg;
    private UtenlandskSykmelding utenlandskSykmelding;
    private String fellesformat;
    private String tssid;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Sykemelding {

        private String id;
        private String msgId;
        private String pasientAktoerId;
        private MedisinskVurdering medisinskVurdering;
        private Boolean skjermesForPasient;
        private Arbeidsgiver arbeidsgiver;
        private List<Periode> perioder;
        private Prognose prognose;
        private String utdypendeOpplysninger;
        private String tiltakArbeidsplassen;
        private String tiltakNAV;
        private String andreTiltak;
        private MeldingTilNAV meldingTilNAV;
        private String meldingTilArbeidsgiver;
        private KontaktMedpasient kontaktMedPasient;
        private LocalDateTime behandletTidspunkt;
        private Behandler behandler;
        private AvsenderSystem avsenderSystem;
        private LocalDate syketilfelleStartDato;
        private LocalDateTime signaturDato;
        private String navnFastlege;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UtenlandskSykmelding {

        private String land;
        private Boolean folkeRegistertAdresseErBrakkeEllerTilsvarende;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Merknad {

        private String type;
        private String beskrivelse;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AvsenderSystem {

        private String navn;
        private String versjon;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Behandler {

        private String fornavn;
        private String mellomnavn;
        private String etternavn;
        private String aktoerId;
        private String fnr;
        private String hpr;
        private String her;
        private Adresse adresse;
        private String tlf;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Adresse {

        private String gate;
        private Integer postnummer;
        private String kommune;
        private String postboks;
        private String land;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KontaktMedpasient {

        private LocalDate kontaktDato;
        private String begrunnelseIkkeKontakt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MeldingTilNAV {

        private Boolean bistandUmiddelbart;
        private String beskrivBistand;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Prognose {

        private Boolean arbeidsforEtterPeriode;
        private Boolean hensynArbeidsplassen;
        private ErIArbeid erIArbeid;
        private ErIkkeIArbeid erIkkeIArbeid;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErIArbeid {

        private Boolean egetArbeidPaSikt;
        private Boolean annetArbeidPaSikt;
        private LocalDate arbeidFOM;
        private LocalDate vurderingsdato;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErIkkeIArbeid {

        private Boolean arbeidsforPaSikt;
        private LocalDate arbeidsforFOM;
        private LocalDate vurderingsdato;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Periode {

        private LocalDate fom;
        private LocalDate tom;
        private AktivitetIkkeMulig aktivitetIkkeMulig;
        private String avventendeInnspillTilArbeidsgiver;
        private Integer behandlingsdager;
        private Gradert gradert;
        private Boolean reisetilskudd;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Gradert {

        private Boolean reisetilskudd;
        private Integer grad;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AktivitetIkkeMulig {

        private MedisinskArsak medisinskArsak;
        private ArbeidsrelatertArsak arbeidsrelatertArsak;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MedisinskArsak {

        private String beskrivelse;
        private List<MedisinskArsakType> arsak;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ArbeidsrelatertArsak {

        private String beskrivelse;
        private List<ArbeidsrelatertArsakType> arsak;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Arbeidsgiver {

        private ArbeidsgiverType harArbeidsgiver;
        private String navn;
        private String yrkesbetegnelse;
        private Integer stillingsprosent;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MedisinskVurdering {

        private Diagnose hovedDiagnose;
        private List<Diagnose> biDiagnoser;
        private Boolean svangerskap;
        private Boolean yrkesskade;
        private LocalDate yrkesskadeDato;
        private AnnenFraversArsak annenFraversArsak;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnnenFraversArsak {

        private String beskrivelse;
        private List<AnnetFravaersArsakType> grunn;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Diagnose {

        private String system;
        private String kode;
        private String tekst;
    }

    public enum AnnetFravaersArsakType {
        GODKJENT_HELSEINSTITUSJON,
        BEHANDLING_FORHINDRER_ARBEID,
        ARBEIDSRETTET_TILTAK,
        MOTTAR_TILSKUDD_GRUNNET_HELSETILSTAND,
        NEDVENDIG_KONTROLLUNDENRSOKELSE,
        SMITTEFARE,
        ABORT,
        UFOR_GRUNNET_BARNLOSHET,
        DONOR,
        BEHANDLING_STERILISERING
    }

    public enum ArbeidsgiverType {
        EN_ARBEIDSGIVER,
        FLERE_ARBEIDSGIVERE,
        INGEN_ARBEIDSGIVER
    }

    public enum MedisinskArsakType {
        TILSTAND_HINDRER_AKTIVITET,
        AKTIVITET_FORVERRER_TILSTAND,
        AKTIVITET_FORHINDRER_BEDRING,
        ANNET
    }

    public enum ArbeidsrelatertArsakType {
        MANGLENDE_TILRETTELEGGING,
        ANNET
    }
}
