package no.nav.testnav.dollysearchservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponse {

    private SearchRequest request;

    private Integer took;
    private Boolean timedOut;
    private SearchHits hits;

    private HttpStatus status;
    private String error;

    public static Mono<SearchResponse> of(WebClientError.Description description) {
        return Mono.just(SearchResponse
                .builder()
                .status(description.getStatus())
                .error(description.getMessage())
                .build());
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchHits {

        private Total total;
        private float maxScore;
        private List<SearchHit> hits;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Total {

        private Long value;
        private String relation;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @SuppressWarnings("java:S116")
    public static class SearchHit {

        private String _index;
        private String _type;
        private String _id;
        private Double _score;
        private Object _source;
    }
}
