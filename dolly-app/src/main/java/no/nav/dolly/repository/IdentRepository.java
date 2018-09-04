package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.Testident;

import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface IdentRepository extends CrudRepository<Testident, String> {

    Testident findByIdent(String ident);
	
	void deleteTestidentsByIdent(Set<String> testident);

	void deleteTestidentByIdent(String testident);
}
