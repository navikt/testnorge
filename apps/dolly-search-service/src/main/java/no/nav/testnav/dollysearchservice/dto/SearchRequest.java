package no.nav.testnav.dollysearchservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import no.nav.testnav.dollysearchservice.domain.ElasticTyper;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequest {

    private List<ElasticTyper> typer;
    private PersonRequest personRequest;

    public List<ElasticTyper> getTyper() {

        if (isNull(typer)) {
            typer = new ArrayList<>();
        }
        return typer;
    }
}
