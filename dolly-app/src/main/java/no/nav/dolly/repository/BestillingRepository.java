package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.Testgruppe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BestillingRepository extends JpaRepository<Bestilling, Long> {

    List<Bestilling> findBestillingByGruppeOrderById(Testgruppe gruppe);

    @Query(value = "from Bestilling b where b.malBestillingNavn is not null order by b.malBestillingNavn")
    Optional<List<Bestilling>> findMalBestilling();
}
