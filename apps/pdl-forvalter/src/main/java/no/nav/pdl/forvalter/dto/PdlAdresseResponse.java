package no.nav.pdl.forvalter.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PdlAdresseResponse {

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
        private Vegadresse vegadresse;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Vegadresse {

        private String matrikkelId;
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
    }
}
