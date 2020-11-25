package no.nav.dolly.common.repository;

import org.springframework.data.repository.CrudRepository;

import no.nav.dolly.domain.jpa.Testident;

public interface IdentTestRepository extends CrudRepository<Testident, Long> {
    void flush();
}
