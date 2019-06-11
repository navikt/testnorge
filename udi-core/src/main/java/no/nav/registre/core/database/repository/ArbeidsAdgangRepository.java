package no.nav.registre.core.database.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import no.nav.registre.core.database.model.Arbeidsadgang;

@Repository
public interface ArbeidsAdgangRepository extends CrudRepository<Arbeidsadgang, Long> {
}
