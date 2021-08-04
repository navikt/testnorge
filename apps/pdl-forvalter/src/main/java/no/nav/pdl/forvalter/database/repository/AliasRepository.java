package no.nav.pdl.forvalter.database.repository;

import no.nav.pdl.forvalter.database.model.DbAlias;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AliasRepository extends CrudRepository<DbAlias, Long> {

    Optional<DbAlias> findByTidligereIdent(String ident);
}
