package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.jpa.Testgruppe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GruppeRepository extends JpaRepository<Testgruppe, Long> {

    Testgruppe save(Testgruppe testgruppe);

    List<Testgruppe> findAllByOrderByNavn();

    List<Testgruppe> findAllByTeamtilhoerighetOrderByNavn(Team team);

    Testgruppe findByNavn(String navn);

    int deleteTestgruppeById(Long id);

    int deleteTestgruppeByTeamtilhoerighetId(Long teamId);
}
