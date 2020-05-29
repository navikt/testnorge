package no.nav.registre.sam.database;

import org.springframework.data.repository.CrudRepository;

import no.nav.registre.sam.domain.database.TSamMelding;

public interface TSamMeldingRepository extends CrudRepository<TSamMelding, Long> {

}
