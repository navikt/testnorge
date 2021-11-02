package no.nav.dolly.bestilling.pdlforvalter.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.resultset.pdlforvalter.falskidentitet.PdlFalskIdentitet;
import no.nav.dolly.domain.resultset.pdlforvalter.utenlandsid.PdlUtenlandskIdentifikasjonsnummer;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Pdldata {

    private PdlKontaktinformasjonForDoedsbo kontaktinformasjonForDoedsbo;
    private List<PdlUtenlandskIdentifikasjonsnummer> utenlandskIdentifikasjonsnummer;
    private PdlFalskIdentitet falskIdentitet;
}
