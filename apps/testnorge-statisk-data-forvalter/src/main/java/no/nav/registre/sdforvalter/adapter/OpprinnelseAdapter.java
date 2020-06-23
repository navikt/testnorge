package no.nav.registre.sdforvalter.adapter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import no.nav.registre.sdforvalter.database.model.OpprinnelseModel;
import no.nav.registre.sdforvalter.database.repository.OpprinnelseRepository;

@Component
@AllArgsConstructor
public class OpprinnelseAdapter {

    private final OpprinnelseRepository repository;

    OpprinnelseModel saveOpprinnelse(String opprinnelse) {
        return repository
                .findByNavn(opprinnelse)
                .orElseGet(() -> repository.save(new OpprinnelseModel(opprinnelse)));
    }
}