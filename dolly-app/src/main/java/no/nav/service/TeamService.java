package no.nav.service;

import ma.glasnost.orika.MapperFacade;
import no.nav.api.resultSet.RsBruker;
import no.nav.api.resultSet.RsTeam;
import no.nav.exceptions.ConstraintViolationException;
import no.nav.exceptions.DollyFunctionalException;
import no.nav.exceptions.NotFoundException;
import no.nav.jpa.Bruker;
import no.nav.jpa.Team;
import no.nav.repository.BrukerRepository;
import no.nav.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TeamService {
	
	@Autowired
	private TeamRepository teamRepository;

	@Autowired
	private BrukerRepository brukerRepository;

	@Autowired
	private BrukerService brukerService;

	@Autowired
	private MapperFacade mapperFacade;

	public Team opprettTeam(RsTeam rsTeam) {
		Bruker eier = brukerService.getBruker(rsTeam.getEier().getNavIdent());

		Team team = mapperFacade.map(rsTeam, Team.class);
		team.setDatoOpprettet(LocalDateTime.now());

//		Team team = Team.builder()
//				.navn(createTeamRequest.getNavn())
//				.beskrivelse(createTeamRequest.getBeskrivelse())
//				.eier(eier)
//				.datoOpprettet(LocalDateTime.now()).build();
		
		return saveToTeamRepository(team);
	}

	public Team fetchTeamById(Long id){
		Team team = teamRepository.findTeamById(id);

		if(team == null) {
			throw new NotFoundException("Team ikke funnet for denne IDen");
		}
		return team;
	}


	public void addMedlemmer(Long teamId, List<RsBruker> navIdenter) {
		Team team = teamRepository.findTeamById(teamId);
		Set<Bruker> nyeMedlemmer = findOrCreateBrukere(navIdenter);
		team.getMedlemmer().addAll(nyeMedlemmer);
		teamRepository.save(team);
	}

	public Team updateTeamInfo(Long teamId, RsTeam teamRequest) {
		Team team = teamRepository.findTeamById(teamId);
		endreTeamInfo(team,teamRequest);

		return saveToTeamRepository(team);
	}

	public Team fjernMedlemmer(Long teamId, List<RsBruker> navIdenter) {
		Team team = teamRepository.findTeamById(teamId);
		if (!team.getMedlemmer().isEmpty()) {
			team.getMedlemmer().removeIf(medlem -> navIdenter.stream().anyMatch(rsBruker -> rsBruker.getNavIdent().equals(medlem.getNavIdent())));
		}

		return saveToTeamRepository(team);
	}

	private Team saveToTeamRepository(Team team) {
		try {
			return teamRepository.save(team);
		} catch (DataIntegrityViolationException e) {
			throw new ConstraintViolationException("Team constraint <unikt teamnavn> er brutt. Teamnavnet er allerede i bruk!");
		} catch (NonTransientDataAccessException e) {
			throw new DollyFunctionalException(e.getRootCause().getMessage(), e);
		}
	}

	private Set<Bruker> findOrCreateBrukere(List<RsBruker> rsBrukere) {
		Set<Bruker> brukere = new HashSet<>();

		for (RsBruker rsBruker : rsBrukere) {
			Bruker bruker = brukerRepository.findBrukerByNavIdent(rsBruker.getNavIdent());

			if (bruker == null) {
				bruker= brukerRepository.save(mapperFacade.map(rsBruker, Bruker.class));
			}

			brukere.add(bruker);
		}
		return brukere;
	}

    private void endreTeamInfo(Team team, RsTeam teamRequest) {
        team.setNavn(teamRequest.getNavn());
        team.setBeskrivelse(teamRequest.getBeskrivelse());
        if (!teamRequest.getEier().equals(team.getEier().getNavIdent())) {
            Bruker nyEier = brukerService.getBruker(teamRequest.getEier().getNavIdent());

            if (nyEier == null) {
                throw new DollyFunctionalException("Bruker med nav-ident " + teamRequest.getEier() + " finnes ikke i Dolly DB");
            }

            team.setEier(nyEier);
        }
    }
}
