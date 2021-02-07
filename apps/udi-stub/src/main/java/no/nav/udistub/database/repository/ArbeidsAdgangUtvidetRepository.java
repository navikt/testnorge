package no.nav.udistub.database.repository;

import no.nav.udistub.database.model.Arbeidsadgang;
import no.nav.udistub.database.model.ArbeidsadgangUtvidet;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArbeidsAdgangUtvidetRepository extends CrudRepository<ArbeidsadgangUtvidet, Long> {
}
