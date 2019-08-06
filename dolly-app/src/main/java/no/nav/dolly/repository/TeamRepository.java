package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends Repository<Team, Long> {

    Team save(Team team);

    //TODO Brukes bare i test
    void saveAll(Iterable<Team> teams);

    Optional<Team> findById(Long id);

    Optional<Team> findByNavn(String navn);

    List<Team> findAllByOrderByNavn();

    List<Team> findTeamsByEierOrderByNavn(Bruker eier);

    List<Team> findByMedlemmerNavIdentOrderByNavn(String navIdent);

    int deleteTeamById(Long id);
}