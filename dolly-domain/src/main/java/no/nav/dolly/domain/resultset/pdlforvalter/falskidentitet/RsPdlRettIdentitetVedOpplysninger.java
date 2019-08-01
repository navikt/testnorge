package no.nav.dolly.domain.resultset.pdlforvalter.falskidentitet;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.pdlforvalter.PdlPersonnavn;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RsPdlRettIdentitetVedOpplysninger extends RsPdlRettIdentitet {

    private LocalDateTime foedselsdato;
    private PdlKjoenn kjoenn;
    private PdlPersonnavn personnavn;
    private List<String> statsborgerskap;
}
