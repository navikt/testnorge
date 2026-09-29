package no.nav.testnav.apps.statusfrontend.fagsystem.krr;

import java.util.List;

public record KrrResourceStatus(
        boolean empty,
        boolean expectedDataPresent,
        ResponseShape responseShape,
        int responseSize,
        boolean hasMessage,
        boolean hasUnregisteredEntry,
        List<String> contactIds
) {

    public KrrResourceStatus {
        contactIds = List.copyOf(contactIds);
    }

    public enum ResponseShape {
        ABSENT, ARRAY, OBJECT, UNKNOWN
    }

    public KrrResourceStatus(boolean empty, boolean expectedDataPresent) {
        this(empty, expectedDataPresent, ResponseShape.UNKNOWN, 0, false, false);
    }

    public KrrResourceStatus(boolean empty, boolean expectedDataPresent, ResponseShape responseShape,
                             int responseSize, boolean hasMessage, boolean hasUnregisteredEntry) {
        this(empty, expectedDataPresent, responseShape, responseSize, hasMessage, hasUnregisteredEntry, List.of());
    }

    @Override
    public String toString() {
        return "KrrResourceStatus[empty=" + empty + ", expectedDataPresent=" + expectedDataPresent
                + ", responseShape=" + responseShape + ", responseSize=" + responseSize
                + ", hasMessage=" + hasMessage + ", hasUnregisteredEntry=" + hasUnregisteredEntry
                + ", contactIdCount=" + contactIds.size() + "]";
    }

    public static KrrResourceStatus emptyStatus() {
        return new KrrResourceStatus(true, false, ResponseShape.ABSENT, 0, false, false);
    }
}
