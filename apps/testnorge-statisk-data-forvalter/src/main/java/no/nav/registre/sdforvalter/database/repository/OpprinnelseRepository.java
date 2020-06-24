package no.nav.registre.sdforvalter.database.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import no.nav.registre.sdforvalter.database.model.OpprinnelseModel;

@Repository
public interface OpprinnelseRepository extends CrudRepository<OpprinnelseModel, Long> {
    Optional<OpprinnelseModel> findByNavn(String navn);
}
