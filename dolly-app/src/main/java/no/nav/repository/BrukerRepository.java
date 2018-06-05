package no.nav.repository;

import no.nav.jpa.Bruker;

import java.util.List;
import org.springframework.data.repository.Repository;

public interface BrukerRepository extends Repository<Bruker, Long>  {
	Bruker save(Bruker bruker);
	
	Bruker findBrukerByNavIdent(String navIdent);

	List<Bruker> findAll();
}
