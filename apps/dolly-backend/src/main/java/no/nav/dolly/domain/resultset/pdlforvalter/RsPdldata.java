package no.nav.dolly.domain.resultset.pdlforvalter;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.pdlforvalter.doedsbo.RsPdlKontaktinformasjonForDoedsbo;
import no.nav.dolly.domain.resultset.pdlforvalter.falskidentitet.RsPdlFalskIdentitet;
import no.nav.dolly.domain.resultset.pdlforvalter.utenlandsid.PdlUtenlandskIdentifikasjonsnummer;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RsPdldata {

    private RsPdlKontaktinformasjonForDoedsbo kontaktinformasjonForDoedsbo;
    private List<PdlUtenlandskIdentifikasjonsnummer> utenlandskIdentifikasjonsnummer;
    private RsPdlFalskIdentitet falskIdentitet;
}
