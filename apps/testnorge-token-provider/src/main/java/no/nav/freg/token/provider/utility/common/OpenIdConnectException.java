package no.nav.freg.token.provider.utility.common;

public class OpenIdConnectException extends RuntimeException {

    public OpenIdConnectException(String message) {
        super(message);
    }

    public OpenIdConnectException(
            String message,
            Throwable cause
    ) {
        super(message, cause);
    }
}
