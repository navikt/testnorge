package no.nav.registre.tp.database.repository;

import org.springframework.data.repository.CrudRepository;

import no.nav.registre.tp.database.models.TPerson;

public interface TPersonRepository extends CrudRepository<TPerson, Integer> {

    TPerson findByFnrFk(String fnr);

}
