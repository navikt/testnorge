package no.nav.registre.sam.database;

import no.nav.registre.sam.domain.database.TSamMelding;
import no.nav.registre.sam.domain.database.TSamVedtak;
import org.springframework.data.repository.CrudRepository;

public interface TSamVedtakRepository extends CrudRepository<TSamVedtak, Number> {}
