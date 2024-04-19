package no.nav.pdl.forvalter.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TestnorgeIdentUtility {

    public boolean isTestnorgeIdent(String ident) {

        return Character.getNumericValue(ident.charAt(3)) >= 8;
    }
}
