package no.nav.dolly.repository;

import no.nav.jpa.BestillingProgress;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface BestillingProgressRepository extends CrudRepository<BestillingProgress, Long> {

    List<BestillingProgress> findBestillingProgressByBestillingsId(Long bestillingsId);
}
