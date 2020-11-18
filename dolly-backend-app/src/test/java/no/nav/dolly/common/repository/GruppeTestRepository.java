package no.nav.dolly.common.repository;

import org.springframework.data.repository.CrudRepository;

import no.nav.dolly.domain.jpa.postgres.Testgruppe;

public interface GruppeTestRepository extends CrudRepository<Testgruppe, Long> {
    void flush();
}
