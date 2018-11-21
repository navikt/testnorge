package no.nav.registre.hodejegeren.exception;

import java.util.List;

import lombok.Getter;

@Getter
public class IkkeFullfoertBehandlingException extends HodejegerenFunctionalException {

    private final List<Long> ids;
    private final Exception history;

    public IkkeFullfoertBehandlingException(String s, List<Long> ids, Exception history) {
        super(s);
        this.ids = ids;
        this.history = history;
    }
}
