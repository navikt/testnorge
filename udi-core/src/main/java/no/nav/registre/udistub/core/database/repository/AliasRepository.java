package no.nav.registre.udistub.core.database.repository;

import no.nav.registre.udistub.core.database.model.Alias;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AliasRepository extends CrudRepository<Alias, Long> {
}
