package no.nav.registre.testnorge.originalpopulasjon.service;

import lombok.AllArgsConstructor;

import no.nav.registre.testnorge.libs.dto.statistikk.v1.StatistikkType;

@AllArgsConstructor
public class Statistikk {

    public static final StatistikkType BARN = StatistikkType.ANDEL_PERSONER_ALDER_0_19;
    public static final Integer barn_min_alder = 0;
    public static final Integer barn_max_alder = 19;

    public static final StatistikkType VOKSEN = StatistikkType.ANDEL_PERSONER_ALDER_20_64;
    public static final Integer voksen_min_alder = 20;
    public static final Integer voksen_max_alder = 64;

    public static final StatistikkType ELDRE = StatistikkType.ANDEL_PERSONER_ALDER_65_;
    public static final Integer eldre_min_alder = 65;
    public static final Integer eldre_max_alder = 100;


}
