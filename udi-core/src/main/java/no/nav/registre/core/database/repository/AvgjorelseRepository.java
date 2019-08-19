package no.nav.registre.core.database.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import no.nav.registre.core.database.model.Avgjorelse;


@Repository
public interface AvgjorelseRepository extends CrudRepository<Avgjorelse, Long> {
    Avgjorelse findAvgjoerelseBySaksnummer(Long saksnummer);
}
