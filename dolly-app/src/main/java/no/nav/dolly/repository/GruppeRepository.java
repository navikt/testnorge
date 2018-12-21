package no.nav.dolly.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.jpa.Testgruppe;

public interface GruppeRepository extends JpaRepository<Testgruppe, Long> {

	@Modifying
	Testgruppe save(Testgruppe testgruppe);

	List<Testgruppe> findAllByOrderByNavn();

	List<Testgruppe> findAllByTeamtilhoerighetOrderByNavn(Team team);

	List<Testgruppe> findAllByOpprettetAvOrderByNavn(Bruker bruker);

	Testgruppe findByNavn(String navn);

	@Modifying
	int deleteTestgruppeById(Long id);

	@Modifying
	int deleteTestgruppeByTeamtilhoerighetId(Long teamId);
}
