package no.nav.testnav.apps.statusfrontend.functionaltest.model;

public enum FunctionalTestErrorCategory {
    AUTHENTICATION,
    TIMEOUT,
    NETWORK,
    VALIDATION,
    DOWNSTREAM_CLIENT,
    DOWNSTREAM_SERVER,
    VERIFICATION_TIMEOUT,
    EXISTING_DATA,
    CLEANUP,
    INTERNAL
}
