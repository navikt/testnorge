package no.nav.registre.skd.exceptions;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@Getter
public class IkkeFullfoertBehandlingExceptionsContainer extends SkdFunctionalException {

    private final Set<Long> ids = new LinkedHashSet<>();
    private final List<String> messages = new ArrayList<>();
    private final List<Throwable> causes = new ArrayList<>();

    public IkkeFullfoertBehandlingExceptionsContainer addIds(List<Long> ids) {
        this.ids.addAll(ids);
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
