package no.nav.registre.sdForvalter.database.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import no.nav.registre.sdForvalter.database.model.KildeSystemModel;

@Repository
public interface KildeSystemRepository extends CrudRepository<KildeSystemModel, Long> {
    Optional<KildeSystemModel> findByNavn(String navn);
}
