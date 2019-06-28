package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.BestillingProgress;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BestillingProgressRepository extends CrudRepository<BestillingProgress, Long> {

    List<BestillingProgress> findBestillingProgressByBestillingIdOrderByBestillingId(Long bestillingId);

    @Modifying
    void deleteByBestillingId(Long bestillingId);
}
