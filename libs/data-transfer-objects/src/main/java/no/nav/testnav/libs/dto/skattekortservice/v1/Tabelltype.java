package no.nav.testnav.libs.dto.skattekortservice.v1;

public enum Tabelltype {
    TREKKTABELL_FOR_PENSJON("trekktabellForPensjon"),
    TREKKTABELL_FOR_LOENN("trekktabellForLoenn");
    private final String value;

    Tabelltype(String v) {
        value = v;
    }

    public String value() {
        return value;
    }
}
