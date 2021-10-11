package no.nav.testnav.apps.brukerservice.exception;

public class UsernameAlreadyTakenException extends RuntimeException {
    public UsernameAlreadyTakenException(String username) {
        super("Bruker navn '" + username + "' er allerde tatt.");
    }
}
