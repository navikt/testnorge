package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {

    @Modifying
    Team save(Team team);

    Optional<Team> findByNavn(String navn);

    List<Team> findAllByOrderByNavn();

    List<Team> findTeamsByEierOrderByNavn(Bruker eier);

    List<Team> findByMedlemmerNavIdentOrderByNavn(String navIdent);

    @Modifying
    int deleteTeamById(Long id);
}