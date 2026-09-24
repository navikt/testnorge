package no.nav.testnav.apps.statusfrontend.fagsystem.krr;

public record KrrResourceStatus(boolean empty, boolean expectedDataPresent) {

    public static KrrResourceStatus emptyStatus() {
        return new KrrResourceStatus(true, false);
    }
}
