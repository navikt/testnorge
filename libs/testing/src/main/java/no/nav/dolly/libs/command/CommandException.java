package no.nav.dolly.libs.command;

import java.net.URI;

public class CommandException extends RuntimeException {

    public CommandException(URI uri, Throwable cause) {
        super("Command to URI %s failed".formatted(uri), cause);
    }

}
