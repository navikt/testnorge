package no.nav.registre.sdForvalter.database.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import no.nav.registre.sdForvalter.database.model.OpprinnelseModel;

@Repository
public interface OpprinnelseRepository extends CrudRepository<OpprinnelseModel, Long> {
    Optional<OpprinnelseModel> findByNavn(String navn);
}
