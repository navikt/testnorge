package no.nav.dolly.domain.resultset.pdlforvalter.doedsbo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsPdlKontaktpersonUtenIdNummer implements PdlSomAdressat {

    private LocalDateTime foedselsdato;
    private PdlPersonnavn navn;
}
