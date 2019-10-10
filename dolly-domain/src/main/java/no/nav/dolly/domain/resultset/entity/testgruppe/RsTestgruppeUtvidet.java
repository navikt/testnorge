package no.nav.dolly.domain.resultset.entity.testgruppe;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.entity.testident.RsTestidentBestillingId;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RsTestgruppeUtvidet extends RsTestgruppe {

    private List<RsTestidentBestillingId> testidenter;

    public List<RsTestidentBestillingId> getTestidenter() {
        if (testidenter == null) {
            testidenter = new ArrayList<>();
        }
        return testidenter;
    }
}
