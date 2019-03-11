package no.nav.registre.sam.database;

import no.nav.registre.sam.domain.database.TPerson;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TPersonRepository extends CrudRepository<TPerson, Number> {
    TPerson findByFnrFK(String fnrFK);
}
