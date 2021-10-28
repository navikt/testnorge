package no.nav.testnav.apps.adresseservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PdlAdresseResponse {

    private Data data;
    private List<ErrorStatus> errors;

    public List<ErrorStatus> getErrors() {
        if (isNull(errors)) {
            errors = new ArrayList<>();
        }
        return errors;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorStatus {

        private String message;
        private List<Location> locations;
        private List<String> path;
        private Extension extensions;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Location {
        private Integer line;
        private Integer column;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Extension {
        private String code;
        private String classification;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data {
        private SokAdresse sokAdresse;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SokAdresse {

        private List<Hits> hits;
        private Long pageNumber;
        private Long totalPages;
        private Long totalHits;

        public List<Hits> getHits() {
            if (isNull(hits)) {
                hits = new ArrayList<>();
            }
            return hits;
        }
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Hits {

        private String score;
        private Matrikkeladresse matrikkeladresse;
        private Vegadresse vegadresse;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Matrikkeladresse {

        private String matrikkelId;
        private String kommunenummer;
        private String gaardsnummer;
        private String bruksnummer;
        private String postnummer;
        private String poststed;
        private String tilleggsnavn;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Vegadresse {

        private String matrikkelId;
        private String adressekode;
        private String adressenavn;
        private Integer husnummer;
        private String husbokstav;
        private String postnummer;
        private String poststed;
        private String kommunenummer;
        private String kommunenavn;
        private String bydelsnummer;
        private String bydelsnavn;
        private String tilleggsnavn;
        private String fylkesnummer;
        private String fylkesnavn;
    }
}
