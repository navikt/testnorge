package no.nav.registre.orgnrservice.consumer.exceptions;

public class OrganisasjonApiException extends RuntimeException {

    public OrganisasjonApiException(
            String message,
            Throwable cause
    ) {
        super(message, cause);
    }

}
