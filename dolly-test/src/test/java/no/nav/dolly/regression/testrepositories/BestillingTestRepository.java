package no.nav.dolly.regression.testrepositories;

import no.nav.dolly.domain.jpa.Bestilling;
import org.springframework.data.repository.CrudRepository;

public interface BestillingTestRepository extends CrudRepository<Bestilling, Long> {

    void deleteAll();

}
