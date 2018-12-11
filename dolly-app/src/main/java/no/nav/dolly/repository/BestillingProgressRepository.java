package no.nav.dolly.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

import no.nav.dolly.domain.jpa.BestillingProgress;

public interface BestillingProgressRepository extends CrudRepository<BestillingProgress, Long> {

    List<BestillingProgress> findBestillingProgressByBestillingId(Long bestillingId);

    List<BestillingProgress> findBestillingProgressByIdentIn(List<String> identer);
}
