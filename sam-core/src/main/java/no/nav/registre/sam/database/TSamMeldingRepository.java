package no.nav.registre.sam.database;

import no.nav.registre.sam.domain.database.TSamMelding;
import org.springframework.data.repository.CrudRepository;

public interface TSamMeldingRepository extends CrudRepository<TSamMelding, Number> {}
