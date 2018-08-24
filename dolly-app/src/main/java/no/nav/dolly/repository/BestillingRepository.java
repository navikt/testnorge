package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.Testgruppe;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BestillingRepository extends JpaRepository<Bestilling, Long> {

    List<Bestilling> findBestillingByGruppe(Testgruppe gruppe);
}
