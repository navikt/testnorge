package no.nav.dolly.libs.texas;

public class TexasException extends RuntimeException {

    TexasException(String message) {
        super(message);
    }

    TexasException(String message, Throwable cause) {
        super(message, cause);
    }

}
