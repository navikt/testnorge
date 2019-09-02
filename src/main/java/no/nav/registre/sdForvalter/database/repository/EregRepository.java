package no.nav.registre.sdForvalter.database.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import no.nav.registre.sdForvalter.database.model.EregModel;

@Repository
public interface EregRepository extends CrudRepository<EregModel, Long> {

    EregModel deleteByOrgnr(String orgnr);

    Optional<EregModel> findByOrgnr(String orgnr);

}
