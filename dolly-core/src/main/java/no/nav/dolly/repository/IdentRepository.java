package no.nav.dolly.repository;

import no.nav.jpa.Testident;

import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface IdentRepository extends CrudRepository<Testident, Long> {


//    void saveAll(List<Testident> testidenter);

    Testident findByIdent(Long ident);
	
	void deleteTestidentsByIdent(Set<Long> testident);
	void deleteTestidentByIdent(Long testident);
}
