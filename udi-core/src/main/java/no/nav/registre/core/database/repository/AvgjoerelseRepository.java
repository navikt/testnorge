package no.nav.registre.core.database.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import no.nav.registre.core.database.model.Avgjoerelse;


@Repository
public interface AvgjoerelseRepository extends CrudRepository<Avgjoerelse, Long> {
    Avgjoerelse findAvgjoerelseBySaksnummer(Long saksnummer);
}
