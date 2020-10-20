package no.nav.dolly.bestilling.pdlforvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.domain.resultset.tpsf.Sivilstand;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SivilstandWrapper {

    private Person person;
    private Sivilstand sivilstand;
}
