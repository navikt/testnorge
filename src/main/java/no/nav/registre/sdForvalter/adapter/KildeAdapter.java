package no.nav.registre.sdForvalter.adapter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import no.nav.registre.sdForvalter.database.model.KildeModel;
import no.nav.registre.sdForvalter.database.repository.KildeRepository;
import no.nav.registre.sdForvalter.domain.Kilde;
import no.nav.registre.sdForvalter.domain.Kilder;

@Component
@AllArgsConstructor
public class KildeAdapter {

    private final KildeRepository repository;

    public Kilder getKilder() {
        return new Kilder(repository.findAll());
    }

    KildeModel saveKilde(Kilde kilde) {
        return repository
                .findByNavn(kilde.getNavn())
                .orElseGet(() -> repository.save(new KildeModel(kilde)));
    }
}