package no.nav.registre.core.database.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import no.nav.registre.core.database.model.opphold.OppholdStatus;

@Repository
public interface OppholdStatusRepository extends CrudRepository<OppholdStatus, Long> {
}
