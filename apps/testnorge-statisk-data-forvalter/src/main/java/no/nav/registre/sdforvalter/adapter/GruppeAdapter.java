package no.nav.registre.sdforvalter.adapter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import no.nav.registre.sdforvalter.database.model.GruppeModel;
import no.nav.registre.sdforvalter.database.repository.GruppeRepository;
import no.nav.registre.sdforvalter.domain.GruppeListe;

@Component
@AllArgsConstructor
public class GruppeAdapter {
    private final GruppeRepository repository;

    public GruppeListe fetchGruppeListe() {
        return new GruppeListe(repository.findAll());
    }

    GruppeModel fetchGruppe(String code) {
        return repository.findByKode(code).orElseThrow(
                () -> new RuntimeException("Finner ikke gruppe = " + code + " i gruppe databasen.")
        );
    }

}