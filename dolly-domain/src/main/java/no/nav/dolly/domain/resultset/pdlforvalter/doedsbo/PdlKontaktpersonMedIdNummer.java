package no.nav.dolly.domain.resultset.pdlforvalter.doedsbo;

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
public class PdlKontaktpersonMedIdNummer implements PdlSomAdressat {

    private String idnummer;
}
