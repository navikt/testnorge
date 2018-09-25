package no.nav.identpool.ident.domain;

public enum Identtype {
    FNR,
    DNR;

    public static Identtype enumFromString(String string) {
        if ("UBESTEMT".equals(string)) {
            return null;
        }
        try {
            return Identtype.valueOf(string.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Identtype må være 'FNR', 'DNR' eller blank.", e);
        }
    }
}
