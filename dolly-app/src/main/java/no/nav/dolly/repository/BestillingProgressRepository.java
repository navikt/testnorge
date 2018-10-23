package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Testident;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface BestillingProgressRepository extends CrudRepository<BestillingProgress, Long> {

    List<BestillingProgress> findBestillingProgressByBestillingId(Long bestillingId);

    List<BestillingProgress> findBestillingProgressByIdentIn(List<String> identer);
}
