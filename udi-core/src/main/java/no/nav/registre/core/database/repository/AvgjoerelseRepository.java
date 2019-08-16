package no.nav.registre.core.database.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import no.nav.registre.core.database.model.PersonAvgjorelse;


@Repository
public interface AvgjoerelseRepository extends CrudRepository<PersonAvgjorelse, Long> {
    PersonAvgjorelse findAvgjoerelseBySaksnummer(Long saksnummer);
}
