package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.pdl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PdlPerson {

    private Data data;
    private List<Error> errors;

    public enum Gruppe {AKTORID, FOLKEREGISTERIDENT, NPID}

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data {

        private HentIdenter hentIdenter;
        private HentPerson hentPerson;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HentIdenter {

        private List<Identer> identer;

        public List<Identer> getIdenter() {
            if (isNull(identer)) {
                identer = new ArrayList<>();
            }
            return identer;
        }
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Identer {

        private String ident;
        private boolean historisk;
        private Gruppe gruppe;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HentPerson {

        private List<Boadresse> bostedsadresse;
        private List<Navn> navn;

        public List<Boadresse> getBostedsadresse() {
            if (isNull(bostedsadresse)) {
                bostedsadresse = new ArrayList<>();
            }
            return bostedsadresse;
        }

        public List<Navn> getNavn() {
            if (isNull(navn)) {
                navn = new ArrayList<>();
            }
            return navn;
        }
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Boadresse {

        private LocalDate angittFlyttedato;
        private String coAdressenavn;
        private LocalDate gyldigFraOgMed;
        private LocalDate gyldigTilOgMed;
        private Vegadresse vegadresse;
        private Folkeregistermetadata folkeregistermetadata;
        private Metadata metadata;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Vegadresse {

        private Long matrikkelId;
        private String husbokstav;
        private String husnummer;
        private String adressenavn;
        private String bruktsenhetsnummer;
        private String tilleggsnavn;
        private String postnummer;
        private String kommunenummer;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Navn{

        private String fornavn;
        private String mellomnavn;
        private String etternavn;
        private String forkortetNavn;
        private String originaltNavn;
        private LocalDate gyldigFraOgMed;
        private Folkeregistermetadata folkeregistermetadata;
        private Metadata metadata;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Metadata {
        private boolean historisk;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Folkeregistermetadata {

        private String aarsak;
        private String kilde;
        private LocalDate ajourholdstidspunkt;
        private LocalDate gyldighetstidspunkt;
        private LocalDate opphoerstidspunkt;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Error {

        private String message;
        private Extensions extensions;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Extensions {

        private String code;
        private String classification;
    }
}