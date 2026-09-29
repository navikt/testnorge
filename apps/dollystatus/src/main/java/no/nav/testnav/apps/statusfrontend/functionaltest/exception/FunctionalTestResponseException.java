package no.nav.testnav.apps.statusfrontend.functionaltest.exception;

public final class FunctionalTestResponseException extends IllegalStateException {

    private final Reason reason;

    public FunctionalTestResponseException(Reason reason) {
        super(reason.name());
        this.reason = reason;
    }

    public Reason reason() {
        return reason;
    }

    public enum Reason {
        KRR_EMPTY_RESPONSE,
        KRR_INVALID_RESPONSE,
        KRR_INVALID_CONTACT_INFORMATION,
        KRR_UNEXPECTED_PERSON,
        KRR_MISSING_CONTACT_ID,
        TPS_INVALID_RESPONSE,
        TPS_INCOMPLETE_ENVIRONMENT_STATUS,
        TPS_ENVIRONMENT_FAILURE,
        TPS_LOOKUP_INVALID_RESPONSE
    }
}
