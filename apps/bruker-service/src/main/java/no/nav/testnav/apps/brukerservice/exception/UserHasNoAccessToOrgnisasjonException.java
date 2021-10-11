package no.nav.testnav.apps.brukerservice.exception;

public class UserHasNoAccessToOrgnisasjonException extends RuntimeException {

    public UserHasNoAccessToOrgnisasjonException() {
        super("Bruker har ikke tilgang til organisasjon");
    }
}
