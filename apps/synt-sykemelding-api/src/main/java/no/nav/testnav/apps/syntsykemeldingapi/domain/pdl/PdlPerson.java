package no.nav.testnav.apps.syntsykemeldingapi.domain.pdl;

import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdlPerson {

    private Data data;
    private List<Error> errors;

    public Data getData() {
        if (isNull(data)){
            data = new Data();
        }
        return data;
    }

    public List<Error> getErrors() {
        if (isNull(errors)) {
            errors = new ArrayList<>();
        }
        return errors;
    }

    public enum Gruppe {AKTORID, FOLKEREGISTERIDENT, NPID}

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data {

        private HentIdenter hentIdenter;
        private HentPerson hentPerson;
    }

    @Builder
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

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Identer {

        private String ident;
        private boolean historisk;
        private Gruppe gruppe;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HentPerson {

        private List<Navn> navn;
        private List<Foedsel> foedsel;

        public List<Navn> getNavn() {
            if (isNull(navn)) {
                navn = new ArrayList<>();
            }
            return navn;
        }

        public List<Foedsel> getFoedsel() {
            if (isNull(foedsel)) {
                foedsel = new ArrayList<>();
            }
            return foedsel;
        }

    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Navn implements WithMetadata {

        private String fornavn;
        private String mellomnavn;
        private String etternavn;
        private String forkortetNavn;
        private String originaltNavn;
        private LocalDate gyldigFraOgMed;
        private Folkeregistermetadata folkeregistermetadata;
        private Metadata metadata;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Foedsel implements WithMetadata{

        private String foedselsaar;
        private LocalDate foedselsdato;
        private String foedeland;
        private String foedested;
        private String foedekommune;
        private Folkeregistermetadata folkeregistermetadata;
        private Metadata metadata;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Folkeregistermetadata {

        private String aarsak;
        private String kilde;
        private LocalDate ajourholdstidspunkt;
        private LocalDate gyldighetstidspunkt;
        private LocalDate opphoerstidspunkt;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Error {

        private String message;
        private Extensions extensions;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Extensions {

        private String code;
        private String classification;
    }
}