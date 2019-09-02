package no.nav.registre.sdForvalter.database.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import no.nav.registre.sdForvalter.database.model.KrrModel;

@Repository
public interface KrrRepository extends CrudRepository<KrrModel, String> {

    Optional<KrrModel> findByFnr(String fnr);
}
