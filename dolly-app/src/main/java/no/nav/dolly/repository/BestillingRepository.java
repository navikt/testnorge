package no.nav.dolly.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.Testgruppe;

public interface BestillingRepository extends JpaRepository<Bestilling, Long> {

    List<Bestilling> findBestillingByGruppeOrderById(Testgruppe gruppe);

    @Query(value = "from Bestilling b where b.malBestillingNavn is not null order by b.malBestillingNavn")
    Optional<List<Bestilling>> findMalBestilling();

    @Modifying
    int deleteByGruppeId(Long gruppeId);
}
