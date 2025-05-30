package no.nav.testnav.libs.data.dollysearchservice.v1;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponse {

    private Long totalHits;
    private String took;
    private Integer side;
    private Integer antall;
    private Integer seed;
    private List<JsonNode> personer;
    private String error;
}