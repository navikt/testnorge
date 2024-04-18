package no.nav.testnav.apps.oppsummeringsdokumentservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.opensearch.action.search.SearchResponse;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryResponse {

    private SearchResponse response;
    private List<Oppsummeringsdokument> dokumenter;
}
