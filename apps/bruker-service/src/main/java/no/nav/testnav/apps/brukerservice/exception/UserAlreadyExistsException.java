package no.nav.testnav.apps.brukerservice.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String id){
        super("Bruker med id '" + id + "' finnes allerde.");
    }
}
