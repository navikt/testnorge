package no.nav.repository;

import no.nav.jpa.Testgruppe;

import java.util.List;
import org.springframework.data.repository.Repository;

public interface GruppeRepository extends Repository<Testgruppe, Long> {
	Testgruppe findById(Long id);
	
	Testgruppe save(Testgruppe testgruppe);

	List<Testgruppe> findAll();

	void deleteTestgruppeById(Long id);
}
