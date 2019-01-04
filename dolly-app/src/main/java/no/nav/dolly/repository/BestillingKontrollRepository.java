package no.nav.dolly.repository;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

import no.nav.dolly.domain.jpa.BestillingKontroll;

public interface BestillingKontrollRepository extends CrudRepository<BestillingKontroll, Long> {

    Optional<BestillingKontroll> findByBestillingIdOrderByBestillingId(Long bestillingId);
}