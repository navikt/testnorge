package no.nav.registre.testnorge.originalpopulasjon.domain;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class Alderskategori {
    Integer antall;
    Aldersspenn aldersspenn;
}
