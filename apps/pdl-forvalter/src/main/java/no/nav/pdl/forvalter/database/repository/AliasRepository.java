package no.nav.pdl.forvalter.database.repository;

import no.nav.pdl.forvalter.database.model.DbAlias;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AliasRepository extends JpaRepository<DbAlias, Long> {

    Optional<DbAlias> findByTidligereIdent(String ident);

    List<DbAlias> findByTidligereIdentIn(List<String> ident);
}
