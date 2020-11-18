package no.nav.dolly.repository.oracle;

import org.springframework.data.repository.Repository;

import no.nav.dolly.domain.jpa.oracle.OraBestilling;

public interface OraBestillingRepository extends Repository<OraBestilling, Long> {

    Iterable<OraBestilling> findAllByOrderByIdAsc();
}
