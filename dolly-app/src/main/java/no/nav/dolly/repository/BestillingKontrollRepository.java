package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.BestillingKontroll;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface BestillingKontrollRepository extends Repository<BestillingKontroll, Long> {

    Optional<BestillingKontroll> save(BestillingKontroll bestillingKontroll);

    Optional<BestillingKontroll> findByBestillingIdOrderByBestillingId(Long bestillingId);
}