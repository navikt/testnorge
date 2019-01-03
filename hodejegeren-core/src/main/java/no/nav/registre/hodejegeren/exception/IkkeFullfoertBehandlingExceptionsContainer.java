package no.nav.registre.hodejegeren.exception;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class IkkeFullfoertBehandlingExceptionsContainer extends HodejegerenFunctionalException {

    private final List<String> ids = new ArrayList<>();
    private final List<String> messages = new ArrayList<>();
    private final List<Throwable> causes = new ArrayList<>();

    public IkkeFullfoertBehandlingExceptionsContainer addIds(List<String> idsWithRange) {
        this.ids.addAll(idsWithRange);
        return this;
    }

    public IkkeFullfoertBehandlingExceptionsContainer addMessage(String message) {
        this.messages.add(message);
        return this;
    }

    @Override
    public String getMessage() {
        return String.format("Messages: %s", getMessages());
    }

    public IkkeFullfoertBehandlingExceptionsContainer addCause(Throwable e) {
        this.causes.add(e);
        return this;
    }
}
