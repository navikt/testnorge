package no.nav.testnav.apps.statusfrontend.fagsystem.brregstub;

public record BrregstubResourceStatus(boolean empty, boolean expectedDataPresent) {

    public static BrregstubResourceStatus emptyStatus() {
        return new BrregstubResourceStatus(true, false);
    }
}
