package no.nav.dolly.common.repository;

import no.nav.dolly.domain.jpa.Testident;
import org.springframework.data.repository.CrudRepository;

public interface IdentTestRepository extends CrudRepository<Testident, Long> {
    void flush();
}
