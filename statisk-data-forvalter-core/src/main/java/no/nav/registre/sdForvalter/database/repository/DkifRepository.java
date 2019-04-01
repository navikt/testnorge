package no.nav.registre.sdForvalter.database.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import no.nav.registre.sdForvalter.database.model.DkifModel;

@Repository
public interface DkifRepository extends CrudRepository<DkifModel, String> {

}
