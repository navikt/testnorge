package no.nav.identpool.ident.domain;

import no.nav.identpool.ident.exception.IllegalIdenttypeException;

public enum Identtype {
    FNR,
    DNR;

    public static Identtype enumFromString(String string) throws IllegalIdenttypeException {
        if (string.equals("UBESTEMT")) {
            return null;
        }
        try {
            return Identtype.valueOf(string.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Identtype må være 'FNR', 'DNR' eller blank.", e);
        }
    }
}
