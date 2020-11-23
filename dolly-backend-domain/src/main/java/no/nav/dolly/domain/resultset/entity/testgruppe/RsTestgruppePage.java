package no.nav.dolly.domain.resultset.entity.testgruppe;

import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsTestgruppePage {

    private Integer antallPages;
    private Integer pageNo;
    private Integer pageSize;
    private Long antallGrupper;
    private List<RsTestgruppe> contents;

    public List<RsTestgruppe> getContents() {
        if (isNull(contents)) {
            contents = new ArrayList<>();
        }
        return contents;
    }
}
