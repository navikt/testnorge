package no.nav.testnav.libs.dto.pdlforvalter.v1;

public enum VergemaalMandattype {

    FOR("Ivareta personens interesser innenfor det personlige og økonomiske området herunder utlendingssaken (kun for EMA)"),
    CMB("Ivareta personens interesser innenfor det personlige og økonomiske området"),
    FIN("Ivareta personens interesser innenfor det økonomiske området"),
    PER("Ivareta personens interesser innenfor det personlige området"),
    ADP("Tilpasset mandat (utfyllende tekst i eget felt)");

    private final String forklaring;

    VergemaalMandattype(String forklaring) {
        this.forklaring = forklaring;
    }
}
