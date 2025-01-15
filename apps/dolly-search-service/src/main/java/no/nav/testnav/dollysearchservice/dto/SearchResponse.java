package no.nav.testnav.dollysearchservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.data.dollysearchservice.v1.ElasticBestilling;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchResponse {

    private Long totalHits;
    private Float score;
    private String took;
    private List<String> identer;
    private List<ElasticBestilling> bestillinger;
    private String error;
}