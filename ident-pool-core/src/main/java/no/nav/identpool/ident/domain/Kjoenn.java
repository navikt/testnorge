package no.nav.identpool.ident.domain;

public enum Kjoenn {
    MANN,
    KVINNE;

    public static Kjoenn enumFromString(String string) {
        if ("UBESTEMT".equals(string)) {
            return null;
        }
        try {
            return Kjoenn.valueOf(string.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Kjoenn må være 'MANN', 'KVINNE' eller blank", e);
        }
    }
}
