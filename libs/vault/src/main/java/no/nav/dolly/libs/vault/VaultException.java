package no.nav.dolly.libs.vault;

public class VaultException extends RuntimeException {

    VaultException(String message) {
        super(message);
    }

    VaultException(String message, Throwable cause) {
        super(message, cause);
    }

}
