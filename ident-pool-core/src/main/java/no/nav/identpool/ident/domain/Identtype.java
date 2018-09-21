package no.nav.identpool.ident.domain;

import no.nav.identpool.ident.exception.IllegalIdenttypeException;

public enum Identtype {
    UBESTEMT,
    FNR,
    DNR;

    public static Identtype enumFromString(String string) throws IllegalIdenttypeException {
        try {
            return Identtype.valueOf(string.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalIdenttypeException("Identtype må være FNR, DNR eller blank.", e);
        }
    }
}
