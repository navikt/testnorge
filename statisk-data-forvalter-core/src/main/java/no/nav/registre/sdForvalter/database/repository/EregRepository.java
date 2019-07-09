package no.nav.registre.sdForvalter.database.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import no.nav.registre.sdForvalter.database.model.EregModel;

@Repository
public interface EregRepository extends CrudRepository<EregModel, Long> {

    EregModel deleteByOrgnr(String orgnr);

}
