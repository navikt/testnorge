package no.nav.registre.testnorge.arena.consumer.rs.response.pdl;

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

        private List<Folkeregisterpersonstatus> folkeregisterpersonstatus;

        public List<Folkeregisterpersonstatus> getFolkeregisterpersonstatus() {
            if (isNull(folkeregisterpersonstatus)) {
                folkeregisterpersonstatus = new ArrayList<>();
            }
            return folkeregisterpersonstatus;
        }
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Folkeregisterpersonstatus {

        private String identifikasjonsnummer;
        private String status;
        private String forenkletStatus;
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
}