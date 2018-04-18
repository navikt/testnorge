package no.nav.repository;

import no.nav.jpa.Testgruppe;
import org.springframework.data.repository.Repository;

public interface GruppeRepository extends Repository<Testgruppe, Long> {
	Testgruppe findById(Long id);
	
	void save(Testgruppe testgruppe);
	void deleteTestgruppeById(Long id);
}
