package no.nav.registre.inst.fasit;

public class FasitException extends RuntimeException {

    public FasitException(String message) {
        super(message);
    }

    public FasitException(String message, Throwable cause) {
        super(message, cause);
    }
}
