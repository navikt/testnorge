package no.nav.brregstub.database.repository;

import no.nav.brregstub.database.domene.Rolleutskrift;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolleutskriftRepository extends JpaRepository<Rolleutskrift, Long> {

    Optional<Rolleutskrift> findByIdent(String ident);

}
