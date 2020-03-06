package no.nav.registre.sdForvalter.adapter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import no.nav.registre.sdForvalter.database.model.OpprinnelseModel;
import no.nav.registre.sdForvalter.database.repository.OpprinnelseRepository;

@Component
@AllArgsConstructor
public class OpprinnelseAdapter {

    private final OpprinnelseRepository repository;

    OpprinnelseModel saveOpprinnelse(String opprinelse) {
        return repository
                .findByNavn(opprinelse)
                .orElseGet(() -> repository.save(new OpprinnelseModel(opprinelse)));
    }
}