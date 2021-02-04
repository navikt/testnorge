package no.nav.udistub.database.repository;

import no.nav.udistub.database.model.opphold.OppholdStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OppholdStatusRepository extends CrudRepository<OppholdStatus, Long> {
}
