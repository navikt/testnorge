package no.nav.pdl.forvalter.utils;

import lombok.experimental.UtilityClass;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@UtilityClass
public class ArtifactUtils {

    public static final String NORGE = "NOR";

    public static boolean isLandkode(String landkode) {

        return isNotBlank(landkode) &&
                (landkode.matches("[A-Z]{3}") ||
                        "???".equals(landkode));
    }

    public static boolean isSpraak(String spraak) {

        return isNotBlank(spraak) && spraak.matches("[A-Z]{2}");
    }
}
