package no.nav.dolly.domain.resultset.pdlforvalter.doedsbo;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PdlKontaktpersonUtenIdNummerSomAdressat {

        private LocalDate foedselsdato;
        private PdlPersonnavn navn;
}
