package no.nav.registre.sdForvalter.adapter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import no.nav.registre.sdForvalter.database.model.KildeSystemModel;
import no.nav.registre.sdForvalter.database.repository.KildeSystemRepository;
import no.nav.registre.sdForvalter.domain.KildeSystem;
import no.nav.registre.sdForvalter.domain.KildeSystemListe;

@Component
@AllArgsConstructor
public class KildeSystemAdapter {

    private final KildeSystemRepository repository;

    public KildeSystemListe getKildeSystemListe() {
        return new KildeSystemListe(repository.findAll());
    }

    KildeSystemModel saveKildeSystem(KildeSystem kildeSystem) {
        return repository
                .findByNavn(kildeSystem.getNavn())
                .orElseGet(() -> repository.save(new KildeSystemModel(kildeSystem)));
    }
}