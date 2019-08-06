package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.jpa.Testgruppe;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface GruppeRepository extends Repository<Testgruppe, Long> {

    Optional<Testgruppe> findById(Long id);

    List<Testgruppe> findAllById(Iterable<Long> ids);

    Testgruppe save(Testgruppe testgruppe);

    List<Testgruppe> saveAll(Iterable<Testgruppe> testgrupper);

    List<Testgruppe> findAllByOrderByNavn();

    List<Testgruppe> findAllByTeamtilhoerighetOrderByNavn(Team team);

    Testgruppe findByNavn(String navn);

    int deleteTestgruppeById(Long id);

    int deleteTestgruppeByTeamtilhoerighetId(Long teamId);
}
