package no.nav.testnav.apps.statusfrontend.functionaltest.model;

public enum FunctionalTestState {
    NOT_RUN,
    RUNNING,
    PREFLIGHT,
    PREFLIGHT_FAILED,
    CREATE,
    CREATE_FAILED,
    VERIFY,
    VERIFY_FAILED,
    VERIFY_TIMEOUT,
    CLEANUP,
    CLEANUP_FAILED,
    OK,
    BLOCKED,
    TECHNICAL_ONLY;

    public boolean isTerminal() {
        return switch (this) {
            case NOT_RUN, PREFLIGHT_FAILED, CREATE_FAILED, VERIFY_FAILED, VERIFY_TIMEOUT,
                 CLEANUP_FAILED, OK, BLOCKED, TECHNICAL_ONLY -> true;
            case RUNNING, PREFLIGHT, CREATE, VERIFY, CLEANUP -> false;
        };
    }
}
