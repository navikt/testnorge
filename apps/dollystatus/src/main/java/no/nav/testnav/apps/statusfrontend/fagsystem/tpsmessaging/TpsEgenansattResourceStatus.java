package no.nav.testnav.apps.statusfrontend.fagsystem.tpsmessaging;

public record TpsEgenansattResourceStatus(
        boolean allEnvironmentsPresent,
        boolean expectedDataPresent,
        boolean inactive,
        boolean hasStartDate,
        boolean hasEndDate,
        boolean activeWithDifferentStartDate
) {

    public TpsEgenansattResourceStatus(boolean allEnvironmentsPresent, boolean expectedDataPresent,
                                       boolean inactive) {
        this(allEnvironmentsPresent, expectedDataPresent, inactive, false, false, false);
    }
}
