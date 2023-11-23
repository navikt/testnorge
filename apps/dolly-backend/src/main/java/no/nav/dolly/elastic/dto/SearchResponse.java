package no.nav.dolly.elastic.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchResponse {

    private Long totalHits;
    private String took;
    private Float score;
    private Integer pageNumber;
    private Integer pageSize;
    private Integer windowSize;
    private List<String> identer;
    private String error;
}