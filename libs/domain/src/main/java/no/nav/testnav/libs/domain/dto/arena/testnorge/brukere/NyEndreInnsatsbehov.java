package no.nav.testnav.libs.domain.dto.arena.testnorge.brukere;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NyEndreInnsatsbehov {

    private String hovedmaal;
    private Kvalifiseringsgrupper kvalifiseringsgruppe;
}
