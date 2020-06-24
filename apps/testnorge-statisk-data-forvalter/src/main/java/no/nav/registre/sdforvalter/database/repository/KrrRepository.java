package no.nav.registre.sdforvalter.database.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import no.nav.registre.sdforvalter.database.model.KrrModel;

@Repository
public interface KrrRepository extends CrudRepository<KrrModel, String> {
}
