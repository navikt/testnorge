package no.nav.registre.sdForvalter.database.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import no.nav.registre.sdForvalter.database.model.KildeModel;

@Repository
public interface KildeRepository extends CrudRepository<KildeModel, Long> {
    List<KildeModel> findByNavnIn(List<String> navns);

    Optional<KildeModel> findByNavn(String navn);
}
