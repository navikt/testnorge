package no.nav.registre.sam.database;

import org.springframework.data.repository.CrudRepository;

import no.nav.registre.sam.domain.database.TPerson;

public interface TPersonRepository extends CrudRepository<TPerson, Long> {

    TPerson findByFnrFK(String fnrFK);
}
