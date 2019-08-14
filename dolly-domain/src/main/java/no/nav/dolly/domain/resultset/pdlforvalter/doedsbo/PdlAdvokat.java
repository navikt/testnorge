package no.nav.dolly.domain.resultset.pdlforvalter.doedsbo;

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
public class PdlAdvokat extends PdlSomAdressat {

    private PdlPersonnavn kontaktperson;
    private String organisasjonsnavn;
    private String organisasjonsnummer;
}
