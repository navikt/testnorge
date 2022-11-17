package no.nav.dolly.domain.resultset.entity.testgruppe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.jpa.Testgruppe;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsTestgruppePage {

    private Integer antallPages;
    private Integer pageNo;
    private Integer pageSize;
    private Long antallElementer;
    private List<RsTestgruppe> contents;
    private Set<Testgruppe> favoritter;

    public List<RsTestgruppe> getContents() {
        if (isNull(contents)) {
            contents = new ArrayList<>();
        }
        return contents;
    }

    public Set<Testgruppe> getFavoritter() {
        if (isNull(favoritter)) {
            favoritter = new HashSet<>();
        }
        return favoritter;
    }
}
