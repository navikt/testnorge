package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response;

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

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data {
        private HentPerson hentPerson;
    }


    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HentPerson {

        private List<Folkeregisterpersonstatus> folkeregisterpersonstatus;
        private List<ForelderBarnRelasjon> forelderBarnRelasjon;

        public List<Folkeregisterpersonstatus> getFolkeregisterpersonstatus() {
            if (isNull(folkeregisterpersonstatus)) {
                folkeregisterpersonstatus = new ArrayList<>();
            }
            return folkeregisterpersonstatus;
        }

        public List<ForelderBarnRelasjon> getForelderBarnRelasjon() {
            if (isNull(forelderBarnRelasjon)) {
                forelderBarnRelasjon = new ArrayList<>();
            }
            return forelderBarnRelasjon;
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
    public static class ForelderBarnRelasjon {
        private String relatertPersonsIdent;
        private String relatertPersonsRolle;
        private String minRolleForPerson;
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