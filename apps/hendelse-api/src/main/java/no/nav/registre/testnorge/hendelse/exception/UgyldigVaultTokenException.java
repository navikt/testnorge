package no.nav.registre.testnorge.hendelse.exception;

public class UgyldigVaultTokenException extends RuntimeException {

    public UgyldigVaultTokenException(String message) {
        super(message);
    }

    public UgyldigVaultTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
