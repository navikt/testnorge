package no.nav.registre.testnorge.domain.dto.arena.testnorge.brukere;

import lombok.*;

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
