package no.nav.registre.sdForvalter.database.repository;

import no.nav.registre.sdForvalter.database.model.KrrModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KrrRepository extends CrudRepository<KrrModel, String> {
}
