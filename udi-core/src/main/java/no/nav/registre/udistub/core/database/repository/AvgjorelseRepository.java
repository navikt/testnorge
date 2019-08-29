package no.nav.registre.udistub.core.database.repository;

import no.nav.registre.udistub.core.database.model.Avgjorelse;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvgjorelseRepository extends CrudRepository<Avgjorelse, Long> {
    Avgjorelse findAvgjoerelseBySaksnummer(Long saksnummer);
}
