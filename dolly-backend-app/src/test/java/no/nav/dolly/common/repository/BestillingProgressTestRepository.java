package no.nav.dolly.common.repository;

import org.springframework.data.repository.CrudRepository;

import no.nav.dolly.domain.jpa.postgres.BestillingProgress;

public interface BestillingProgressTestRepository extends CrudRepository<BestillingProgress, Long> {
    void flush();
}
