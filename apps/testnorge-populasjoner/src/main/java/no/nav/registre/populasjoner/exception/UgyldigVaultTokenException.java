package no.nav.registre.populasjoner.exception;

public class UgyldigVaultTokenException extends RuntimeException {

    public UgyldigVaultTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public UgyldigVaultTokenException(String message) {
        super(message);
    }
}
