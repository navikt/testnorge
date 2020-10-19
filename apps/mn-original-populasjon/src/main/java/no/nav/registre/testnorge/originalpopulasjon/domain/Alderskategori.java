package no.nav.registre.testnorge.originalpopulasjon.domain;

import lombok.AllArgsConstructor;
import lombok.Value;

import no.nav.registre.testnorge.libs.dto.statistikk.v1.StatistikkType;

@Value
@AllArgsConstructor
public class Alderskategori {

    Integer minAlder;
    Integer maxAlder;
    Integer antall;

}
