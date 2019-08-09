package no.nav.dolly.common.repository;

import no.nav.dolly.domain.jpa.Testgruppe;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GruppeTestRepository extends CrudRepository<Testgruppe, Long> {
    void flush();
}
