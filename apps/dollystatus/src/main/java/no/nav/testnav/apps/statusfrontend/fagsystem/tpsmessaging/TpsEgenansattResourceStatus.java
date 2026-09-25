package no.nav.testnav.apps.statusfrontend.fagsystem.tpsmessaging;

public record TpsEgenansattResourceStatus(
        boolean allEnvironmentsPresent,
        boolean expectedDataPresent,
        boolean inactive
) {
}
