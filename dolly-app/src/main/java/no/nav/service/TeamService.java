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

	@Autowired
	BrukerService brukerService;

	public Team opprettTeam(CreateTeamRequest createTeamRequest) {
		Bruker eier = brukerService.getBruker(createTeamRequest.getEierensNavIdent());

		Team team = Team.builder()
				.navn(createTeamRequest.getNavn())
				.beskrivelse(createTeamRequest.getBeskrivelse())
				.eier(eier)
				.datoOpprettet(LocalDateTime.now()).build();
		
		return saveToTeamRepository(team);
	}


	public void addMedlemmer(Long teamId, Set<String> navIdenter) {
		Team team = teamRepository.findTeamById(teamId);
		Set<Bruker> nyeMedlemmer = findOrCreateBrukere(navIdenter);
		team.getMedlemmer().addAll(nyeMedlemmer);
		teamRepository.save(team);
	}

	public Team updateTeamInfo(Long teamId, CreateTeamRequest teamRequest) {
		Team team = teamRepository.findTeamById(teamId);
		endreTeamInfo(team,teamRequest);

		return saveToTeamRepository(team);
	}

	public Team fjernMedlemmer(Long teamId, Set<String> navIdenter) {
		Team team = teamRepository.findTeamById(teamId);
		if (team.getMedlemmer() != null && !team.getMedlemmer().isEmpty()) {
			team.getMedlemmer().removeIf(medlem -> navIdenter.stream().anyMatch(fjernIdent -> fjernIdent.equals(medlem.getNavIdent())));
		}
		return saveToTeamRepository(team);
	}

	private Team saveToTeamRepository(Team team) {
		try {
			return teamRepository.save(team);
		} catch (NonTransientDataAccessException e) {
			throw new DollyFunctionalException(e.getRootCause().getMessage(), e);
		}
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

    private void endreTeamInfo(Team team,CreateTeamRequest teamRequest) {
        team.setNavn(teamRequest.getNavn());
        team.setBeskrivelse(teamRequest.getBeskrivelse());
        if (!teamRequest.getEierensNavIdent().equals(team.getEier().getNavIdent())) {
            Bruker nyEier = brukerRepository.findBrukerByNavIdent(teamRequest.getEierensNavIdent());
            if (nyEier == null) {
                throw new DollyFunctionalException("Bruker med nav-ident " + teamRequest.getEierensNavIdent() + " finnes ikke i Dolly DB");
            }
            team.setEier(nyEier);
        }
    }
}
