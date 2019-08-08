package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.BestillingProgress;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface BestillingProgressRepository extends Repository<BestillingProgress, Long> {

    Optional<BestillingProgress> save(BestillingProgress bestillingProgress);

    List<BestillingProgress> findBestillingProgressByBestillingIdOrderByBestillingId(Long bestillingId);

    void deleteByBestillingId(Long bestillingId);
}
