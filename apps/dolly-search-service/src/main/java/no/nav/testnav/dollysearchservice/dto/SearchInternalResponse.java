package no.nav.testnav.dollysearchservice.dto;

import tools.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchInternalResponse {

    private Long totalHits;
    private String took;
    private Integer side;
    private Integer antall;
    private Integer seed;
    private List<JsonNode> personer;
    private String error;

    public List<JsonNode> getPersoner() {

        if (isNull(personer)) {
            return new ArrayList<>();
        }
        return personer;
    }
}