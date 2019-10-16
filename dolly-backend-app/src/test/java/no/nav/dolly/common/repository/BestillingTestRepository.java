package no.nav.dolly.common.repository;

import no.nav.dolly.domain.jpa.Bestilling;
import org.springframework.data.repository.CrudRepository;

public interface BestillingTestRepository extends CrudRepository<Bestilling, Long> {
    void flush();
}
