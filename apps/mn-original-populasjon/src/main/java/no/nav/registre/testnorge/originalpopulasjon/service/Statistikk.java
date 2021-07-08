package no.nav.registre.testnorge.originalpopulasjon.service;

import no.nav.testnav.libs.dto.statistikkservice.v1.StatistikkType;

public class Statistikk {

    private Statistikk() {
        throw new IllegalStateException("Statistikk class");
    }

    public static final StatistikkType BARN = StatistikkType.ANDEL_PERSONER_ALDER_0_19;
    public static final Integer BARN_MIN_ALDER = 0;
    public static final Integer BARN_MAX_ALDER = 19;

    public static final StatistikkType VOKSEN = StatistikkType.ANDEL_PERSONER_ALDER_20_64;
    public static final Integer VOKSEN_MIN_ALDER = 20;
    public static final Integer VOKSEN_MAX_ALDER = 64;

    public static final StatistikkType ELDRE = StatistikkType.ANDEL_PERSONER_ALDER_OVER_65;
    public static final Integer ELDRE_MIN_ALDER = 65;
    public static final Integer ELDRE_MAX_ALDER = 100;
}
