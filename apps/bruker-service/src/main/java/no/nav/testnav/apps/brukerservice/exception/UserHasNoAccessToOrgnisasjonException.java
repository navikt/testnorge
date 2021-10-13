package no.nav.testnav.apps.brukerservice.exception;

public class UserHasNoAccessToOrgnisasjonException extends RuntimeException {

    public UserHasNoAccessToOrgnisasjonException(String organisasjonsnummer) {
        super("Bruker har ikke tilgang til organisasjon " + organisasjonsnummer + ".");
    }
}
