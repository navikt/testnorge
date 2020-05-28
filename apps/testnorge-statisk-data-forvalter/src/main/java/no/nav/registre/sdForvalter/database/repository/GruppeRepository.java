package no.nav.registre.sdForvalter.database.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import no.nav.registre.sdForvalter.database.model.GruppeModel;

@Repository
public interface GruppeRepository extends CrudRepository<GruppeModel, Long> {
    Optional<GruppeModel> findByKode(String kode);
}
