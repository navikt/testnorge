package no.nav.dolly.repository;

import java.util.Set;
import org.springframework.data.repository.CrudRepository;

import no.nav.dolly.domain.jpa.Testident;

public interface IdentRepository extends CrudRepository<Testident, String> {

    Testident findByIdent(String ident);

    void deleteTestidentsByIdent(Set<String> testident);

    void deleteTestidentByIdent(String testident);
}
