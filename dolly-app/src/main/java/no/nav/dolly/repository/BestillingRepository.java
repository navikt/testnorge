package no.nav.dolly.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.Testgruppe;

public interface BestillingRepository extends JpaRepository<Bestilling, Long> {

    List<Bestilling> findBestillingByGruppeOrderById(Testgruppe gruppe);
}
