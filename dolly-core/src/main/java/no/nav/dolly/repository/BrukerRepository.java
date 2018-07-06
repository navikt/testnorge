package no.nav.dolly.repository;

import no.nav.jpa.Bruker;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface BrukerRepository extends CrudRepository<Bruker, Long> {
	Bruker save(Bruker bruker);

	Bruker findBrukerByNavIdent(String navIdent);

	List<Bruker> findByNavIdentIn(List<String> navIdenter);

	List<Bruker> findAll();
}
