package no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.pdl;

import lombok.*;

import java.util.List;
import java.util.Map;

@Builder
@Getter
@Setter
@NoArgsConstructor
public class GraphqlVariables {

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private String fieldName;
        private Map<String, String> searchRule;

    }


    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Paging{
        private int pageNumber;
        private int resultsPerPage;
    }

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Criteria{
        private List<Object> and;
    }

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Root{
        private Paging paging;
        private Criteria criteria;
    }
}