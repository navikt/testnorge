package no.nav.testnav.dollysearchservice.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

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
    private Map<String, JsonNode> personer;
    private String error;
}