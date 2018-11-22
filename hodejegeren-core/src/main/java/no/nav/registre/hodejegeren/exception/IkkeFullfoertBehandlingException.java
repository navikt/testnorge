package no.nav.registre.hodejegeren.exception;

import java.util.List;

import lombok.Getter;

@Getter
public class IkkeFullfoertBehandlingException extends HodejegerenFunctionalException {

    private final List<Long> ids;

    public IkkeFullfoertBehandlingException(String message, Throwable cause, List<Long> ids) {
        super(message, cause);
        this.ids = ids;
    }
}
