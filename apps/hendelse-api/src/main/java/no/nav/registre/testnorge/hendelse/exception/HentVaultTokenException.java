package no.nav.registre.testnorge.hendelse.exception;

public class HentVaultTokenException extends RuntimeException {

    public HentVaultTokenException(String message) {
        super(message);
    }

    public HentVaultTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
