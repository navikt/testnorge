package no.nav.dolly.repository;

import no.nav.jpa.Bruker;
import no.nav.jpa.Team;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface TeamRepository extends CrudRepository<Team, Long> {
	
	Team save(Team team);
	
	Team findTeamById(Long teamId);

	List<Team> findAll();

	List<Team> findTeamsByEier(Bruker eier);

	List<Team> findByMedlemmer_NavIdent(String navIdent);


//	@Query("from Team tm"
//			+ " join Bruker br where tm.m"
//			+ " or medlemmer "
//	)
//	List<Team> findTeamByEierEllerMedlemmer(Bruker bruker);

//	List<Team> findByMedlemmer_NavIdent(String navIdent)

}
