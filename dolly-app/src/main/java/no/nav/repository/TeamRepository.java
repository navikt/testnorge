package no.nav.repository;

import no.nav.jpa.Team;
import org.springframework.data.repository.Repository;

public interface TeamRepository extends Repository<Team, Long> {
	
	Team save(Team team);
	
	Team findTeamById(Long teamId);
}
