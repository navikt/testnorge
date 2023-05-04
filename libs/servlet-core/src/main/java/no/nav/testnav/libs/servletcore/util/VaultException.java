package no.nav.testnav.libs.servletcore.util;

public class VaultException extends RuntimeException {

    VaultException(String message) {
        super(message);
    }

    VaultException(String message, Throwable cause) {
        super(message, cause);
    }

}
