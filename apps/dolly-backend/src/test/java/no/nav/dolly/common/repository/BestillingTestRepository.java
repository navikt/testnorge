package no.nav.dolly.common.repository;

import org.springframework.data.repository.CrudRepository;

import no.nav.dolly.domain.jpa.Bestilling;

public interface BestillingTestRepository extends CrudRepository<Bestilling, Long> {
    void flush();
}
