package no.nav.udistub.database.repository;

import no.nav.udistub.database.model.Arbeidsadgang;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArbeidsAdgangRepository extends CrudRepository<Arbeidsadgang, Long> {
}
