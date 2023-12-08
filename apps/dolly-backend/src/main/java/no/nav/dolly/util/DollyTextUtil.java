package no.nav.dolly.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DollyTextUtil {

    private static final String INFO_STARTET = "Oppretting startet";
    private static final String INFO_VENTER_SYSTEM = "Info: " + INFO_STARTET + " mot %s ...";
    private static final String SYNC_START = "Info: Synkronisering mot %s startet ... %d ms";
    private static final String INFO_SYNC = "Synkronisering mot";

    public static String getInfoTextSystem(String system) {

        return INFO_VENTER_SYSTEM.formatted(system);
    }

    public static String getInfoText() {

        return INFO_STARTET;
    }

    public static String getSyncTextSystem(String system, Long millis) {

        return SYNC_START.formatted(system, millis);
    }

    public static String getInfoSync() {

        return INFO_SYNC;
    }
}
