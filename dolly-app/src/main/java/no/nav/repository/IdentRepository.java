package no.nav.repository;

import no.nav.jpa.Testident;
import org.springframework.data.repository.Repository;

import java.util.Set;

public interface IdentRepository extends Repository<Testident, Long>{

    void save(Testident testident);
    
    Testident findByIdent(Long ident);
	
	void deleteTestidentsByIdent(Set<Long> testident);
	void deleteTestidentByIdent(Long testident);
}
