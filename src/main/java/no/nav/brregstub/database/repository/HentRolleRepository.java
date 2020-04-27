package no.nav.brregstub.database.repository;

import no.nav.brregstub.database.domene.HentRolle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HentRolleRepository extends JpaRepository<HentRolle, Long> {

    Optional<HentRolle> findByOrgnr(Integer orgnr);

}
