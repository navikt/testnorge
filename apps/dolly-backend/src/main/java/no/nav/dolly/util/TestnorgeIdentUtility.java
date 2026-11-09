package no.nav.dolly.util;

import lombok.experimental.UtilityClass;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@UtilityClass
public class TestnorgeIdentUtility {

    public static boolean isTestnorgeIdent(String ident) {

        return isNotBlank(ident) &&
                Character.getNumericValue(ident.charAt(2)) >= 8;
    }
}
