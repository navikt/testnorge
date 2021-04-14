package no.nav.dolly.domain.resultset.entity.testident;

import lombok.Builder;
import lombok.Data;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsTestgruppe;

@Data
@Builder
public class RsWhereAmI {

    private RsTestgruppe gruppe;
    private String identHovedperson;
    private String identNavigerTil;
    private int sidetall;
}
