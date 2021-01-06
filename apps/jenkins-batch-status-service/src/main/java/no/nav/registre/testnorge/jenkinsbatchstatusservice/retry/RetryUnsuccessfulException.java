package no.nav.registre.testnorge.jenkinsbatchstatusservice.retry;

public class RetryUnsuccessfulException extends RuntimeException {
    public RetryUnsuccessfulException() {
        super("Operasjonen ble aldri fullført.");
    }

    public RetryUnsuccessfulException(Throwable cause) {
        super("Operasjonen ble aldri fullført.", cause);
    }
}
