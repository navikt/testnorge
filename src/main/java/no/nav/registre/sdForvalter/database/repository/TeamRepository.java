package no.nav.registre.sdForvalter.database.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import no.nav.registre.sdForvalter.database.model.Team;

@Repository
public interface TeamRepository extends CrudRepository<Team, Long> {

    Optional<Team> findByNavn(String navn);
}
