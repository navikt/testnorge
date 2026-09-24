package no.nav.testnav.apps.statusfrontend.fagsystem.pensjon;

public record PensjonResourceStatus(boolean empty, boolean expectedDataPresent) {

    public static PensjonResourceStatus emptyStatus() {
        return new PensjonResourceStatus(true, false);
    }
}
