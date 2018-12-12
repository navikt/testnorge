package no.nav.dolly.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;

public interface TeamRepository extends JpaRepository<Team, Long> {
	
	Team save(Team team);

	Optional<Team> findByNavn(String navn);

	List<Team> findAll();

	List<Team> findTeamsByEier(Bruker eier);

	List<Team> findByMedlemmerNavIdent(String navIdent);
}