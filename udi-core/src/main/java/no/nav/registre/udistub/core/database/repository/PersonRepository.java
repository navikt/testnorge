package no.nav.registre.udistub.core.database.repository;

import no.nav.registre.udistub.core.database.model.Person;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRepository extends CrudRepository<Person, Long> {

	Optional<Person> findByIdent(String ident);
}
