package no.nav.dolly.domain.resultset.pdlforvalter.doedsbo;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdlKontaktpersonUtenIdNummer {

        private LocalDate foedselsdato;
        private PdlPersonnavn navn;
}
