package no.nav.testnav.apps.statusfrontend.fagsystem.udi;

public record UdiResourceStatus(boolean empty, boolean expectedDataPresent) {

    public static UdiResourceStatus emptyStatus() {
        return new UdiResourceStatus(true, false);
    }
}
