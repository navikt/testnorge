package no.nav.testnav.libs.domain.dto.eregmapper.v1;

public enum Maalform {
    BOOKMAAL("B"),
    NYNORSK("N"),
    B("B"),
    N("N");

    private final String form;

    Maalform(String form) {
        this.form = form;
    }

    public String getForm() {
        return form;
    }
}
