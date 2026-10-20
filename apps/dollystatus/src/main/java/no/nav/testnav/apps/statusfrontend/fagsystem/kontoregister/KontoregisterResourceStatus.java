package no.nav.testnav.apps.statusfrontend.fagsystem.kontoregister;

public record KontoregisterResourceStatus(boolean empty, boolean expectedDataPresent) {

    public static KontoregisterResourceStatus emptyStatus() {
        return new KontoregisterResourceStatus(true, false);
    }
}
