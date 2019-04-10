package no.nav.registre.sdForvalter.database.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import no.nav.registre.sdForvalter.database.model.TpsModel;

@Repository
public interface TpsRepository extends CrudRepository<TpsModel, String> {

}
