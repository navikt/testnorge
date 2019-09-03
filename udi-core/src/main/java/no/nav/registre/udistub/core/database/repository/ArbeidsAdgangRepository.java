package no.nav.registre.udistub.core.database.repository;

import no.nav.registre.udistub.core.database.model.Arbeidsadgang;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArbeidsAdgangRepository extends CrudRepository<Arbeidsadgang, Long> {
}
