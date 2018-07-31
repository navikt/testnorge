package no.nav.dolly.repository;

import no.nav.jpa.Team;
import no.nav.jpa.Testgruppe;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestGruppeRepository extends JpaRepository<Testgruppe, Long> {

	Testgruppe save(Testgruppe testgruppe);

	List<Testgruppe> findAll();

	List<Testgruppe> findAllByTeamtilhoerighet(Team team);

	Testgruppe findByNavn(String navn);

	void deleteTestgruppeById(Long id);
}
