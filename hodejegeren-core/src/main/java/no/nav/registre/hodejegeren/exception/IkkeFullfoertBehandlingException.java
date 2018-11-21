package no.nav.registre.hodejegeren.exception;

import java.util.List;

import lombok.Getter;

@Getter
public class IkkeFullfoertBehandlingException extends HodejegerenFunctionalException {

    private List<Long> ids;

    public IkkeFullfoertBehandlingException() {
        super();
    }

    public IkkeFullfoertBehandlingException(String s) {
        super(s);
    }

    public IkkeFullfoertBehandlingException(String s, List<Long> ids) {
        super(s);
        this.ids = ids;
    }
}
