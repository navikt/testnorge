package no.nav.registre.sdForvalter.adapter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import no.nav.registre.sdForvalter.database.model.GruppeModel;
import no.nav.registre.sdForvalter.database.repository.GruppeRepository;
import no.nav.registre.sdForvalter.domain.GruppeListe;

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