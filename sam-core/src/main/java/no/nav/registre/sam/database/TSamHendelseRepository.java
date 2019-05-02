package no.nav.registre.sam.database;

import org.springframework.data.repository.CrudRepository;

import no.nav.registre.sam.domain.database.TSamHendelse;

public interface TSamHendelseRepository extends CrudRepository<TSamHendelse, Long> {

}
