package no.nav.registre.ereg.provider.rs.request;

public enum Maalform {
    BOOKMAAL("B"),
    NYNORSK("N"),
    B("B"),
    N("N");

    private String form;

    Maalform(String form) {
        this.form = form;
    }

    public String getForm() {
        return form;
    }
}
