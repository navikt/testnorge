package no.nav.testnav.libs.dto.arena.testnorge.brukere;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NyEndreInnsatsbehov {

    private String hovedmaal;
    private Kvalifiseringsgrupper kvalifiseringsgruppe;
}
