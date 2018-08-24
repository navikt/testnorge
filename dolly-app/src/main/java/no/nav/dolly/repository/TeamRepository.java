package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface TeamRepository extends CrudRepository<Team, Long> {
	
	Team save(Team team);
	
	Team findTeamById(Long teamId);

	List<Team> findAll();

	List<Team> findTeamsByEier(Bruker eier);

	List<Team> findByMedlemmer_NavIdent(String navIdent);

}
