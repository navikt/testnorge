package no.nav.pdl.forvalter.repository;

import no.nav.pdl.forvalter.domain.DbPerson;

import java.util.Optional;

public interface PersonRepository {//extends CrudRepository<DbPerson, Long> {

    Optional<DbPerson> findByIdent(String ident);
}
