package no.nav.testnav.apps.tenorsearchservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import tools.jackson.databind.JsonNode;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenorOversiktOrganisasjonResponse {

    private HttpStatus status;
    private Data data;
    private String query;
    private String error;

    @lombok.Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data {

        private Integer treff;
        private Integer rader;
        private Integer offset;
        private Integer nesteSide;
        private Integer seed;
        private List<Organisasjon> organisasjoner;
    }

    @lombok.Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Organisasjon {

        private String navn;
        private String organisasjonsnummer;
        private JsonNode tenorMetadata;
        private JsonNode tenorRelasjoner;
        private JsonNode brregKildedata;
        private List<String> kilder;
    }
}