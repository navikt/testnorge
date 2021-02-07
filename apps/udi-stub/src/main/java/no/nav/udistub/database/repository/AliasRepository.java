package no.nav.udistub.database.repository;

import no.nav.udistub.database.model.Alias;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AliasRepository extends CrudRepository<Alias, Long> {
}
