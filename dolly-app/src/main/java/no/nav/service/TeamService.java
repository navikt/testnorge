package no.nav.service;

import no.nav.api.request.CreateTeamRequest;
import no.nav.exceptions.DollyFunctionalException;
import no.nav.jpa.Bruker;
import no.nav.jpa.Team;
import no.nav.repository.BrukerRepository;
import no.nav.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
public class TeamService {
	
	@Autowired
	TeamRepository teamRepository;
	@Autowired
	BrukerRepository brukerRepository;
	
	public Team opprettTeam(CreateTeamRequest createTeamRequest) {
		Bruker eier = findBrukerByNavIdent(createTeamRequest.getEierensNavIdent());
		Team team = Team.builder()
				.navn(createTeamRequest.getNavn())
				.beskrivelse(createTeamRequest.getBeskrivelse())
				.eier(eier)
				.datoOpprettet(LocalDateTime.now()).build();
		
		return saveToTeamRepository(team);
	}
	
	private Bruker findBrukerByNavIdent(String eierensNavIdent) {
		Bruker eier = brukerRepository.findBrukerByNavIdent(eierensNavIdent);
		if (eier == null) {
			throw new DollyFunctionalException("Eierens NAV-Ident eksisterer ikke i Dolly databasen.");
		}
		return eier;
	}
	
	private Team saveToTeamRepository(Team team) {
		try {
			return teamRepository.save(team);
		} catch (NonTransientDataAccessException e) {
			throw new DollyFunctionalException(e.getRootCause().getMessage(), e);
		}
	}
	
	public void addMedlemmer(Long teamId, Set<String> navIdenter) {
		Team team = teamRepository.findTeamById(teamId);
		Set<Bruker> nyeMedlemmer = findOrCreateBrukere(navIdenter);
		team.addMedlemmer(nyeMedlemmer);
		teamRepository.save(team);
	}
	
	private Set<Bruker> findOrCreateBrukere(Set<String> navIdenter) {
		Set<Bruker> brukere = new HashSet<>();
		for (String navIdent:navIdenter ) {
			Bruker bruker = brukerRepository.findBrukerByNavIdent(navIdent);
			if (bruker == null) {
				bruker= brukerRepository.save(new Bruker(navIdent));
			}
			brukere.add(bruker);
		}
		return brukere;
	}
}
