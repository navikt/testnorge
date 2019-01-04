package no.nav.dolly.repository;

import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;

import no.nav.dolly.domain.jpa.BestillingProgress;

public interface BestillingProgressRepository extends CrudRepository<BestillingProgress, Long> {

    List<BestillingProgress> findBestillingProgressByBestillingIdOrderByBestillingId(Long bestillingId);

    @Modifying
    void deleteByBestillingId(Long bestillingId);
}
