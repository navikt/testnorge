package no.nav.registre.sam.database;

import org.springframework.data.repository.CrudRepository;

import no.nav.registre.sam.domain.database.TSamVedtak;

public interface TSamVedtakRepository extends CrudRepository<TSamVedtak, Long> {

}
