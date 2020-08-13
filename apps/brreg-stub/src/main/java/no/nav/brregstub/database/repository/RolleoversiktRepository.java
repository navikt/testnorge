package no.nav.brregstub.database.repository;

import no.nav.brregstub.database.domene.Rolleoversikt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolleoversiktRepository extends JpaRepository<Rolleoversikt, Long> {

    Optional<Rolleoversikt> findByIdent(String ident);

}
