package no.nav.registre.sam.database;

import no.nav.registre.sam.domain.database.TSamHendelse;
import org.springframework.data.repository.CrudRepository;

public interface TSamHendelseRepository extends CrudRepository<TSamHendelse, Number> {}
