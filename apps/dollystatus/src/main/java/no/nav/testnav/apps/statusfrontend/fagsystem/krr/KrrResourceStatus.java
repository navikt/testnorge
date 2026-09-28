package no.nav.testnav.apps.statusfrontend.fagsystem.krr;

public record KrrResourceStatus(
        boolean empty,
        boolean expectedDataPresent,
        ResponseShape responseShape,
        int responseSize,
        boolean hasMessage,
        boolean hasUnregisteredEntry
) {

    public enum ResponseShape {
        ABSENT, ARRAY, OBJECT, UNKNOWN
    }

    public KrrResourceStatus(boolean empty, boolean expectedDataPresent) {
        this(empty, expectedDataPresent, ResponseShape.UNKNOWN, 0, false, false);
    }

    public static KrrResourceStatus emptyStatus() {
        return new KrrResourceStatus(true, false, ResponseShape.ABSENT, 0, false, false);
    }
}
