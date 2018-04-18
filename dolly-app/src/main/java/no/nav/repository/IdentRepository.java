package no.nav.repository;

import no.nav.jpa.Testident;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface IdentRepository extends Repository<Testident, Long>{

    void save(Testident testident);
    
    Testident findByIdent(Long ident);
	
	void deleteTestidentByIdentAndTestgruppeId(Long testident, Long testgruppeId);
}
