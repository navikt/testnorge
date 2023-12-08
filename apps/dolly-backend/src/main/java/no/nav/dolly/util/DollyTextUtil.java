package no.nav.dolly.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DollyTextUtil {

    private static final String INFO_STARTET = "startet";
    private static final String INFO_VENTER_SYSTEM = "Info: Oppretting " + INFO_STARTET + " mot %s ...";
    private static final String SYNC_START = "Info: Synkronisering mot %s " + INFO_STARTET + "... %d ms";

    public static String getInfoTextSystem(String system) {

        return INFO_VENTER_SYSTEM.formatted(system);
    }

    public static String getInfoText() {

        return INFO_STARTET;
    }

    public static String getSyncTextSystem(String system, Long millis) {

        return SYNC_START.formatted(system, millis);
    }
}
