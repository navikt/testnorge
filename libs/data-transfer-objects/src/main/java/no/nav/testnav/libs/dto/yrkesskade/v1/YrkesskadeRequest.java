package no.nav.testnav.libs.dto.yrkesskade.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class YrkesskadeRequest {

    private String skadelidtIdentifikator;
    private RolleType rolletype;
    private String innmelderIdentifikator;
    private InnmelderRolletype innmelderrolle;
    private Klassifisering klassifisering;
    private String paaVegneAv;
    private Tidstype tidstype;
    private String referanse;
    private FerdigstillSak ferdigstillSak;
}
