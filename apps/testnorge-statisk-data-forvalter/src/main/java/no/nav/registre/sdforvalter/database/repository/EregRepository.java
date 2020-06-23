package no.nav.registre.sdforvalter.database.repository;

import no.nav.registre.sdforvalter.database.model.EregModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EregRepository extends CrudRepository<EregModel, String> {

}
