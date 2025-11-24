package no.nav.testnav.libs.dto.arena.testnorge.brukere;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NyEndreInnsatsbehovFeil {
    private String personident;
    private String miljoe;
    private String nyEndreInnsatsbehovFeilstatus;
    private String melding;
}
