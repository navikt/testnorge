package no.nav.registre.udistub.core.database.repository;

import no.nav.registre.udistub.core.database.model.opphold.OppholdStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OppholdStatusRepository extends CrudRepository<OppholdStatus, Long> {
}
