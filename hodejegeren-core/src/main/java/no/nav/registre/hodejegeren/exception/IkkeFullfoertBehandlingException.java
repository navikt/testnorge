package no.nav.registre.hodejegeren.exception;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import lombok.Getter;

@Getter
public class IkkeFullfoertBehandlingException extends HodejegerenFunctionalException {

    private final Set<Long> ids;
    private final List<String> messages;

    public IkkeFullfoertBehandlingException(List<Long> ids, String message) {
        this.ids = new LinkedHashSet<>();
        this.ids.addAll(ids);
        this.messages = new ArrayList<>();
        addMessage(message);
    }

    public void addIds(List<Long> ids) {
        this.ids.addAll(ids);
    }

    public void addMessage(String message) {
        this.messages.add(message);
    }
}
