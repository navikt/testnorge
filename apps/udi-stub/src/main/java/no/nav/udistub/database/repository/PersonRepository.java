package no.nav.udistub.database.repository;

import no.nav.udistub.database.model.Person;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRepository extends CrudRepository<Person, Long> {

	Optional<Person> findByIdent(String ident);
}
