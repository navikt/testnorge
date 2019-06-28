package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.BestillingKontroll;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface BestillingKontrollRepository extends CrudRepository<BestillingKontroll, Long> {

    Optional<BestillingKontroll> findByBestillingIdOrderByBestillingId(Long bestillingId);
}