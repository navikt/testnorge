package no.nav.testnav.apps.personexportapi.consumer.dto;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

@Getter
public enum Sivilstatus {

    UGIFT("1", "UGIF"),
    GIFT("2", "GIFT"),
    ENKE_ELLER_ENKEMANN("3", "ENKE"),
    SKILT("4", "SKIL"),
    SEPARERT("5", "SEPR"),
    REGISTRERT_PARTNER("6", "REPA"),
    SEPARERT_PARTNER("7", "SEPA"),
    SKILT_PARTNER("8", "SKPA"),
    GJENLEVENDE_PARTNER("9", "GJPA");

    private static Map<String, Sivilstatus> map = new HashMap<>();

    static {
        for (Sivilstatus sivilstatus : Sivilstatus.values()) {
            map.put(sivilstatus.sivilstandKode, sivilstatus);
        }
    }

    private final String sivilstandKode;
    private final String kodeverkskode;

    Sivilstatus(final String sivilstandKode, String kodeverkkode) {
        this.sivilstandKode = sivilstandKode;
        this.kodeverkskode = kodeverkkode;
    }

    public static Sivilstatus lookup(String sivilstatusKode) {
        return map.getOrDefault(sivilstatusKode, UGIFT);
    }

    public static boolean exists(String kodeverkkode) {
        return map.containsKey(kodeverkkode);
    }
}
