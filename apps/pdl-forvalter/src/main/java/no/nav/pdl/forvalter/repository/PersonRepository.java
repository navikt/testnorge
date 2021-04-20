package no.nav.pdl.forvalter.repository;

import no.nav.pdl.forvalter.domain.entity.DbPerson;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRepository extends CrudRepository<DbPerson, Long> {

    Optional<DbPerson> findByIdent(String ident);
}
