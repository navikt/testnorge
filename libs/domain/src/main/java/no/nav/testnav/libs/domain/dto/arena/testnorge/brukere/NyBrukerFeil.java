package no.nav.testnav.libs.domain.dto.arena.testnorge.brukere;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NyBrukerFeil {

    private String personident;
    private String miljoe;
    private String nyBrukerFeilstatus;
    private String melding;
}
