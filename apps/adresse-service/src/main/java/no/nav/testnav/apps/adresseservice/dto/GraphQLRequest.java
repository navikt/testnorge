package no.nav.testnav.apps.adresseservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GraphQLRequest {
    private String query;
    private Map<String, Object> variables;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Paging {

        private Long pageNumber;
        private Long resultsPerPage;
        private List<SortBy> sortBy;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SortBy {

        private String fieldName;
        private String direction;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Criteria {

        private String fieldName;
        private Map<String, Object> searchRule;
    }
}