package no.nav.udistub.database.repository;

import no.nav.udistub.database.model.Avgjorelse;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvgjorelseRepository extends CrudRepository<Avgjorelse, Long> {
}
