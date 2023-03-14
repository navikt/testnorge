package no.nav.udistub.database.repository;

import no.nav.udistub.database.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

	Optional<Person> findByIdent(String ident);
}
