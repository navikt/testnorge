package no.nav.registre.testnorge.jenkinsbatchstatusservice.retry;

public class RetryUnsuccessfulException extends RuntimeException {
    public RetryUnsuccessfulException(String message) {
        super(message);
    }

    public RetryUnsuccessfulException(String message, Throwable cause) {
        super(message, cause);
    }
}
