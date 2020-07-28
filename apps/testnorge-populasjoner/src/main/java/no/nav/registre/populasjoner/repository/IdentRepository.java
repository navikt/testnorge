package no.nav.registre.populasjoner.repository;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface IdentRepository extends CrudRepository<IdentModel, Long> {

    Optional<IdentModel> findByFnr(String fnr);
}
