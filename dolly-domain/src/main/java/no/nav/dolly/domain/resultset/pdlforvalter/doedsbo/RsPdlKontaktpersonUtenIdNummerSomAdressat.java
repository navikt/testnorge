package no.nav.dolly.domain.resultset.pdlforvalter.doedsbo;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RsPdlKontaktpersonUtenIdNummerSomAdressat {

    private LocalDateTime foedselsDato;
    private PdlPersonnavn navn;
}
