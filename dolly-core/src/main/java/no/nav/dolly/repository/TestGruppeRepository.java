package no.nav.dolly.repository;

import no.nav.jpa.Testgruppe;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface TestGruppeRepository extends CrudRepository<Testgruppe, Long> {

	Testgruppe save(Testgruppe testgruppe);

	List<Testgruppe> findAll();

	Testgruppe findByNavn(String navn);

	void deleteTestgruppeById(Long id);
}
