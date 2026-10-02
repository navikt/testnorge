package no.nav.testnav.apps.statusfrontend.functionaltest.exception;

import lombok.Getter;

@Getter
public final class FunctionalTestResponseException extends IllegalStateException {

    private final Reason reason;

    public FunctionalTestResponseException(Reason reason) {
        super(reason.name());
        this.reason = reason;
    }

    public enum Reason {
        KRR_EMPTY_RESPONSE,
        KRR_INVALID_RESPONSE,
        KRR_INVALID_CONTACT_INFORMATION,
        KRR_UNEXPECTED_PERSON,
        KRR_MISSING_CONTACT_ID
    }
}
