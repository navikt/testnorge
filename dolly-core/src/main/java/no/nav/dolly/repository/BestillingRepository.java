package no.nav.dolly.repository;

import no.nav.jpa.Bestilling;
import no.nav.jpa.Testgruppe;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BestillingRepository extends JpaRepository<Bestilling, Long> {

    List<Bestilling> findBestillingByGruppe(Testgruppe gruppe);
}
