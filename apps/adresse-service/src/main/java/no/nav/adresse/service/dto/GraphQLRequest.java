package no.nav.adresse.service.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

        private String pageNumber;
        private String resultsPerPage;
        private Map<SortBy, String> sortBy;
    }

    public enum SortBy {

        fieldName, direction;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Criteria {

        private String fieldName;
        private Map<SearchRule, String> searchRule;
    }

    public enum SearchRule {

        fuzzy, equals, from, to;
    }
}