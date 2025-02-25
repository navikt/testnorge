package no.nav.testnav.dollysearchservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequest {

    private no.nav.testnav.libs.data.dollysearchservice.v1.SearchRequest request;
    private org.opensearch.action.search.SearchRequest query;
}
