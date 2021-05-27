package no.nav.pdl.forvalter.database.repository;

import no.nav.pdl.forvalter.database.model.DbPerson;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRepository extends CrudRepository<DbPerson, Long> {

    Optional<DbPerson> findByIdent(String ident);

    @Modifying
    int deleteByIdent(String ident);

    boolean existsByIdent(String ident);
}
