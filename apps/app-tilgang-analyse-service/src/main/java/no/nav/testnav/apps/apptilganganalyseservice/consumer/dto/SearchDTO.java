package no.nav.testnav.apps.apptilganganalyseservice.consumer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Builder
@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class SearchDTO {
    @JsonProperty("total_count")
    Integer totalCount;
    @JsonProperty("incomplete_results")
    Boolean incompleteResults;
    List<ItemDTO> items;
}
