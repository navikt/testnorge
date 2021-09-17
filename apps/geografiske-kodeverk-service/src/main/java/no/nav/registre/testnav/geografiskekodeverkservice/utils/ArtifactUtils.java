package no.nav.registre.testnav.geografiskekodeverkservice.utils;

import lombok.experimental.UtilityClass;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@UtilityClass
public class ArtifactUtils {

    public static void isKommunenummer(String kommunenummer) {
        if (isNotBlank(kommunenummer) && !kommunenummer.matches("[0-9]{4}")) {
            throw new IllegalArgumentException("Ugyldig kommunenummer");
        }
    }

    public static void isLandkode(String landkode) {
        if (isNotBlank(landkode) && !landkode.matches("[A-Z]{3}")) {
            throw new IllegalArgumentException("Ugyldig landkode");
        }
    }

    public static void isPoststed(String poststed) {
        if (isNotBlank(poststed) && !poststed.matches("[ÆØÅA-Z]+")){
            throw new IllegalArgumentException("Ugyldig poststed");
        }
    }
}
