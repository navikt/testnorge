package no.nav.registre.testnorge.sykemelding.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.dto.sykemelding.v1.UtdypendeOpplysningerDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

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
    private String id;
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

    public List<String> getVedlegg() {

        if (isNull(vedlegg)) {
            vedlegg = new ArrayList<>();
        }
        return vedlegg;
    }

    public List<Merknad> getMerknader() {

        if (isNull(merknader)) {
            merknader = new ArrayList<>();
        }
        return merknader;
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
        private Map<String, Map<String, SporsmalSvar>> utdypendeOpplysninger;
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

        public List<Periode> getPerioder() {

            if (isNull(perioder)) {
                perioder = new ArrayList<>();
            }
            return perioder;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SporsmalSvar {

        private String sporsmal;
        private String svar;
        private List<UtdypendeOpplysningerDTO.Restriksjon> restriksjoner;
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
        private String hensynArbeidsplassen;
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

        public List<MedisinskArsakType> getArsak() {

            if (isNull(arsak)) {
                arsak = new ArrayList<>();
            }
            return arsak;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ArbeidsrelatertArsak {

        private String beskrivelse;
        private List<ArbeidsrelatertArsakType> arsak;

        public List<ArbeidsrelatertArsakType> getArsak() {

            if (isNull(arsak)) {
                arsak = new ArrayList<>();
            }
            return arsak;
        }
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

        public List<Diagnose> getBiDiagnoser() {

            if (isNull(biDiagnoser)) {
                biDiagnoser = new ArrayList<>();
            }
            return biDiagnoser;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnnenFraversArsak {

        private String beskrivelse;
        private List<AnnetFravaersArsakType> grunn;

        public List<AnnetFravaersArsakType> getGrunn() {

            if (isNull(grunn)) {
                grunn = new ArrayList<>();
            }
            return grunn;
        }
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
}
