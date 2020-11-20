package no.nav.registre.testnorge.domain.dto.arena.testnorge.brukere;

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
public class NyEndreInnsatsbehovFeil {
    private String personident;
    private String miljoe;
    private String nyEndreInnsatsbehovFeilstatus;
    private String melding;
}
