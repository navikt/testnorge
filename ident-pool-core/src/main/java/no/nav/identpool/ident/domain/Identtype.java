package no.nav.identpool.ident.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import no.nav.identpool.ident.exception.IllegalIdenttypeException;

@Getter
@AllArgsConstructor
public enum Identtype {
    UBESTEMT("ubestemt"),
    FNR("fnr"),
    DNR("dnr");

    private String type;

    public static Identtype enumFromString(String string) throws IllegalIdenttypeException {
        try {
            return Identtype.valueOf(string.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalIdenttypeException(e.getMessage() + " - Identtype må være FNR, DNR eller blank.");
        }
    }
}
