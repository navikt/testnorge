package no.nav.dolly.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DollyTextUtil {

    private static final String INFO_STARTET = "Oppretting startet";
    private static final String INFO_VENTER_SYSTEM = "Info: " + INFO_STARTET + " mot %s ...";
    private static final String INFO_GENERERING_STARTET = "Info: Venter på generering av sykemelding ...";

    public static String getInfoTextSystem(String system) {

        return INFO_VENTER_SYSTEM.formatted(system);
    }

    public static boolean containsInfoText(String text) {

        return text.contains(INFO_STARTET) ||
                text.contains(INFO_GENERERING_STARTET);
    }

    public static String getGenereringStartet() {

        return INFO_GENERERING_STARTET;
    }
}
