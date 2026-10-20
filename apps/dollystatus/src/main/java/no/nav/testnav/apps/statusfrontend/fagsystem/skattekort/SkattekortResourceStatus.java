package no.nav.testnav.apps.statusfrontend.fagsystem.skattekort;

public record SkattekortResourceStatus(
        boolean empty,
        boolean expectedTaxCardPresent,
        boolean notTaxCard
) {

    public static SkattekortResourceStatus emptyStatus() {
        return new SkattekortResourceStatus(true, false, false);
    }
}
