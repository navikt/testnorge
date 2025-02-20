package no.nav.dolly.opensearch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.minidev.json.annotate.JsonIgnore;
import no.nav.dolly.elastic.ElasticTyper;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponse {

    private RegistreResponseStatus registreSearchResponse;
    private no.nav.testnav.libs.data.dollysearchservice.v1.SearchResponse dollySearchResponse;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegistreResponseStatus {

        private Long totalHitsBestillinger;
        private Integer score;
        private String took;
        private Integer antall;
        private Integer side;
        private Integer antallIdenter;
        private Integer seed;
        private List<ElasticTyper> registre;
        private String error;
        @JsonIgnore
        private List<String> identer;
    }
}
