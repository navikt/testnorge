package no.nav.registre.sdForvalter.database.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import no.nav.registre.sdForvalter.database.model.TpsIdentModel;

@Repository
public interface TpsIdenterRepository extends CrudRepository<TpsIdentModel, String> {

}
