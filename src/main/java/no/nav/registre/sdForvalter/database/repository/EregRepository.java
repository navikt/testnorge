package no.nav.registre.sdForvalter.database.repository;

import no.nav.registre.sdForvalter.database.model.EregModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EregRepository extends CrudRepository<EregModel, String> {

}
