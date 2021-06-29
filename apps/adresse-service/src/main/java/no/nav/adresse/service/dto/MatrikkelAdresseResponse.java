package no.nav.adresse.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatrikkelAdresseResponse {

    private Data data;

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data {
        private SokAdresse sokAdresse;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SokAdresse{

        private List<Hits> hits;
        private Long pageNumber;
        private Long totalPages;
        private Long totalHits;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Hits {

        private String score;
        private Matrikkeladresse matrikkeladresse;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Matrikkeladresse {

        private String matrikkelId;
        private String kommunenummer;
        private String gardsnummer;
        private String bruksnummer;
        private String postnummer;
        private String poststed;
        private String tilleggsnavn;
    }
}
