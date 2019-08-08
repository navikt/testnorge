package no.nav.dolly.testrepositories;

import no.nav.dolly.domain.jpa.Testident;
import org.springframework.data.repository.CrudRepository;

public interface IdentTestRepository extends CrudRepository<Testident, Long> {

    void deleteAll();
}
