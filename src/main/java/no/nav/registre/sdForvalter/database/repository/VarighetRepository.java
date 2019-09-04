package no.nav.registre.sdForvalter.database.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import no.nav.registre.sdForvalter.database.model.Varighet;

@Repository
public interface VarighetRepository extends CrudRepository<Varighet, Long> {
    Optional<Varighet> findByTeam_Navn(String name);
}
