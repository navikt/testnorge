package no.nav.dolly.domain.resultset.tpsf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.Tags;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DollyPerson {

    private String ident;

    private List<Tags> tags;
    private Testident.Master master;
    private boolean isOrdre;

    private Bruker bruker;

    public List<Tags> getTags() {
        if (isNull(tags)) {
            tags = new ArrayList<>();
        }
        return tags;
    }
}
