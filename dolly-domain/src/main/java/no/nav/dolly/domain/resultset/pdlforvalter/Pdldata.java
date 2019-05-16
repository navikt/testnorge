package no.nav.dolly.domain.resultset.pdlforvalter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.pdlforvalter.doedsbo.PdlKontaktinformasjonForDoedsbo;
import no.nav.dolly.domain.resultset.pdlforvalter.utenlandsid.PdlUtenlandskIdentifikasjonsnummer;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pdldata {

    private PdlKontaktinformasjonForDoedsbo kontaktinformasjonForDoedsbo;
    private PdlUtenlandskIdentifikasjonsnummer utenlandskIdentifikasjonsnummer;
}
