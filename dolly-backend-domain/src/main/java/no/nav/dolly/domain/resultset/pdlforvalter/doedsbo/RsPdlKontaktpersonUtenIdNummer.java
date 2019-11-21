package no.nav.dolly.domain.resultset.pdlforvalter.doedsbo;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.pdlforvalter.PdlPersonnavn;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsPdlKontaktpersonUtenIdNummer extends PdlSomAdressat {

    private LocalDateTime foedselsdato;
    private PdlPersonnavn navn;
}
