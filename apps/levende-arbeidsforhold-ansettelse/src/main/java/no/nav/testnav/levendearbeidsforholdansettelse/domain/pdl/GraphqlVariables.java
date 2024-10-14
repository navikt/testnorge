package no.nav.testnav.levendearbeidsforholdansettelse.domain.pdl;

import lombok.*;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
public class GraphqlVariables {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private String fieldName;
        private Map<String, Object> searchRule;

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Paging{
        private int pageNumber;
        private int resultsPerPage;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Criteria{
        private List<Object> and;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Root{
        private Paging paging;
        private Criteria criteria;
    }
}