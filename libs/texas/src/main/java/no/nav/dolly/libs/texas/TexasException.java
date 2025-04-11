package no.nav.dolly.libs.texas;

/**
 * <p>TexasException is a custom exception class that extends RuntimeException.</p>
 * <p>This exception is thrown when there is an error related to the Texas service.</p>
 */
public class TexasException extends RuntimeException {

    TexasException(String message) {
        super(message);
    }

    TexasException(String message, Throwable cause) {
        super(message, cause);
    }

}
