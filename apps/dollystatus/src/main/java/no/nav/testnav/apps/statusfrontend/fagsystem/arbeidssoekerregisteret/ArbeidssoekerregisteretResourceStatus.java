package no.nav.testnav.apps.statusfrontend.fagsystem.arbeidssoekerregisteret;

public record ArbeidssoekerregisteretResourceStatus(boolean empty, boolean expectedDataPresent) {

    public static ArbeidssoekerregisteretResourceStatus emptyStatus() {
        return new ArbeidssoekerregisteretResourceStatus(true, false);
    }
}
