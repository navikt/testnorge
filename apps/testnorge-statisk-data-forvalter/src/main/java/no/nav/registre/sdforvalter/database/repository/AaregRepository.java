package no.nav.registre.sdforvalter.database.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import no.nav.registre.sdforvalter.database.model.AaregModel;

@Repository
public interface AaregRepository extends CrudRepository<AaregModel, String> {

}
