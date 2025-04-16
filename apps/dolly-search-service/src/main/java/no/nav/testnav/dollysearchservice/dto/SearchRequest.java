package no.nav.testnav.dollysearchservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.data.dollysearchservice.v1.PersonRequest;
import no.nav.testnav.libs.data.dollysearchservice.v1.ElasticTyper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequest {

    private Integer side;
    private Integer antall;
    private Integer seed;

    private PersonRequest personRequest;

    private org.opensearch.action.search.SearchRequest query;
    private SearchRequest request;

    private List<ElasticTyper> registreRequest;

    private List<String> miljoer;

    private Set<String> identer;

    public Set<String> getIdenter() {

        if (isNull(identer)) {
            identer = new HashSet<>();
        }
        return identer;
    }

    public List<String> getMiljoer() {

        if (isNull(miljoer)) {
            miljoer = new ArrayList<>();
        }
        return miljoer;
    }

    public List<ElasticTyper> getRegistreRequest() {

        if (isNull(registreRequest)) {
            registreRequest = new ArrayList<>();
        }
        return registreRequest;
    }
}
