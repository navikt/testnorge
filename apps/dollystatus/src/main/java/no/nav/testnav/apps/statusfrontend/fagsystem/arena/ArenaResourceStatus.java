package no.nav.testnav.apps.statusfrontend.fagsystem.arena;

public record ArenaResourceStatus(
        boolean empty,
        boolean active,
        boolean expectedDataPresent,
        boolean inactive
) {

    public static ArenaResourceStatus emptyStatus() {
        return new ArenaResourceStatus(true, false, false, false);
    }
}
