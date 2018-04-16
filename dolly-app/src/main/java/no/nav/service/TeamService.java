package no.nav.service;

import no.nav.api.request.CreateTeamRequest;
import no.nav.jpa.Bruker;
import no.nav.jpa.Team;
import no.nav.repository.BrukerRepository;
import no.nav.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TeamService {
	
	@Autowired
	TeamRepository teamRepository;
	@Autowired
	BrukerRepository brukerRepository;
	
	public void opprettTeam(CreateTeamRequest createTeamRequest) {
		Bruker eier = brukerRepository.findBrukerByNavIdent(createTeamRequest.getEierensNavIdent());
		Team team = Team.builder()
				.navn(createTeamRequest.getNavn())
				.beskrivelse(createTeamRequest.getBeskrivelse())
				.eier(eier)
				.datoOpprettet(LocalDateTime.now()).build();
		
		teamRepository.save(team);
	}
}
