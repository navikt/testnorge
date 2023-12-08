package no.nav.dolly.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DollyTextUtil {

    private static final String INFO_VENTER = "Oppretting startet";
    private static final String INFO_VENTER_SYSTEM = "Info: " + INFO_VENTER + " mot %s ...";

    public static String getInfoTextSystem(String system) {

        return INFO_VENTER_SYSTEM.formatted(system);
    }

    public static String getInfoText() {

        return INFO_VENTER;
    }
}
