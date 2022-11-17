package no.nav.dolly.domain.resultset.entity.testgruppe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsTestgruppeMedBestillingPage {

    private Integer antallPages;
    private Integer pageNo;
    private Integer pageSize;
    private Long antallElementer;
    private List<RsTestgruppeMedBestillingId> contents;

    public List<RsTestgruppeMedBestillingId> getContents() {
        if (isNull(contents)) {
            contents = new ArrayList<>();
        }
        return contents;
    }
}
