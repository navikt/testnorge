package no.nav.registre.hodejegeren.exception;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class IkkeFullfoertBehandlingExceptionsContainer extends HodejegerenFunctionalException {

    private final Set<Long> ids= new LinkedHashSet<>();
    private final List<String> messages= new ArrayList<>();

    public void addIds(List<Long> ids) {
        this.ids.addAll(ids);
    }

    public void addMessage(String message) {
        this.messages.add(message);
    }
}
