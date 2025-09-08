package no.nav.dolly.domain.resultset.entity.testgruppe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
    private List<RsTestgruppe> favoritter;
}
