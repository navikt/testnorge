package no.nav.registre.testnorge.originalpopulasjon.domain;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class Alderskategori {
    Integer minAlder;
    Integer maxAlder;
    Integer antall;
}
