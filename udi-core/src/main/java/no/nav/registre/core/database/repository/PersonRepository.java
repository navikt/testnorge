package no.nav.registre.core.database.repository;

import no.nav.registre.core.database.model.Person;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PersonRepository extends CrudRepository<Person, String> {
}
