package no.nav.dolly.elastic.dto;

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

    private int totalHits;
    private int pageNumber;
    private int pageSize;
    private int windowSize;
    private List<String> identer;
}