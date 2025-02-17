package no.nav.testnav.libs.data.dollysearchservice.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequest {

    private PersonRequest personRequest;

    private Set<String> identer;

    public Set<String> getIdenter() {

        if (isNull(identer)) {
            identer = new HashSet<>();
        }
        return identer;
    }
}
