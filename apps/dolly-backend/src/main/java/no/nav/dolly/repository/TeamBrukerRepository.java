package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.TeamBruker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamBrukerRepository extends JpaRepository<TeamBruker, TeamBruker.TeamBrukerId> {
    void deleteAllById_TeamId(Long idTeamId);
}