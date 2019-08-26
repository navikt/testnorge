package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.Team;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends Repository<Team, Long> {

    Team save(Team team);

    Optional<Team> findById(Long id);

    Optional<Team> findByNavn(String navn);

    List<Team> findAllByOrderByNavn();

    List<Team> findByMedlemmerNavIdentOrderByNavn(String navIdent);

    int deleteTeamById(Long id);
}