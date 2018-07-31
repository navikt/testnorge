package no.nav.service;

import ma.glasnost.orika.MapperFacade;
import no.nav.exceptions.ConstraintViolationException;
import no.nav.exceptions.DollyFunctionalException;
import no.nav.exceptions.NotFoundException;
import no.nav.freg.security.oidc.common.OidcTokenAuthentication;
import no.nav.jpa.Testgruppe;
import no.nav.resultSet.RsBruker;
import no.nav.resultSet.RsOpprettTeam;
import no.nav.resultSet.RsTeam;
import no.nav.jpa.Bruker;
import no.nav.jpa.Team;
import no.nav.dolly.repository.BrukerRepository;
import no.nav.dolly.repository.TeamRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	@Transactional
	public RsTeam opprettTeam(RsOpprettTeam rsTeam) {
		Team team = mapperFacade.map(rsTeam, Team.class);
		team.setDatoOpprettet(LocalDate.now());

		OidcTokenAuthentication auth = (OidcTokenAuthentication) SecurityContextHolder.getContext().getAuthentication();
		Bruker currentBruker = brukerService.fetchBruker(auth.getPrincipal());

		team.setEier(currentBruker);
		team.setMedlemmer(new HashSet<>(Arrays.asList(currentBruker)));

		Team savedTeam = saveToTeamRepository(team);
		return mapperFacade.map(savedTeam, RsTeam.class);
	}

	public Team fetchTeamById(Long id){
		Team team = teamRepository.findTeamById(id);
		if(team == null) {
			throw new NotFoundException("Team ikke funnet for denne IDen");
		}
		return team;
	}

	public void deleteTeam(Long id){
		teamRepository.deleteById(id);
	}

	@Transactional
	public RsTeam addMedlemmer(Long teamId, List<RsBruker> navIdenter) {
		Team team = fetchTeamById(teamId);
		team.getMedlemmer().addAll(mapperFacade.mapAsList(navIdenter, Bruker.class));

		Team changedTeam = saveToTeamRepository(team);
		return mapperFacade.map(changedTeam, RsTeam.class);
	}

	@Transactional
	public RsTeam addMedlemmerByNavidenter(Long teamId, List<String> navIdenter) {
		Team team = fetchTeamById(teamId);
		List<Bruker> brukere = brukerRepository.findByNavIdentIn(navIdenter);

		team.getMedlemmer().addAll(mapperFacade.mapAsList(brukere, Bruker.class));

		Team changedTeam = saveToTeamRepository(team);
		return mapperFacade.map(changedTeam, RsTeam.class);
	}

	@Transactional
	public RsTeam fjernMedlemmer(Long teamId, List<String> navIdenter) {
		Team team = fetchTeamById(teamId);
		if (!team.getMedlemmer().isEmpty()) {
			team.getMedlemmer().removeIf(medlem -> navIdenter.contains(medlem.getNavIdent()));
		}

		Team changedTeam = saveToTeamRepository(team);
		return mapperFacade.map(changedTeam, RsTeam.class);
	}

	@Transactional
	public RsTeam updateTeamInfo(Long teamId, RsTeam teamRequest) {
		Team team = fetchTeamById(teamId);

		team.setNavn(teamRequest.getNavn());
		team.setBeskrivelse(teamRequest.getBeskrivelse());
		team.setDatoOpprettet(teamRequest.getDatoOpprettet());

		Set<Bruker> medlemmer = mapperFacade.mapAsSet(teamRequest.getMedlemmer(), Bruker.class);
		team.setMedlemmer(medlemmer);

	    Bruker endretEier = brukerService.fetchBruker(teamRequest.getEierNavIdent());
	    team.setEier(endretEier);

	    //Set<Testgruppe> grupper = mapperFacade.mapAsSet(teamRequest.getGrupper(), Testgruppe.class);
	    //team.setGrupper(grupper);

		Team endretTeam = saveToTeamRepository(team);
		return mapperFacade.map(endretTeam, RsTeam.class);
	}

	public List<Team> fetchTeamsByMedlemskapInTeams(String navIdent){
		return teamRepository.findByMedlemmer_NavIdent(navIdent);
	}

	private Team saveToTeamRepository(Team team) {
		try {
			return teamRepository.save(team);
		} catch (DataIntegrityViolationException e) {
			throw new ConstraintViolationException("En Team DB constraint er brutt! Kan ikke lagre Team. Error: " + e.getMessage());
		} catch (NonTransientDataAccessException e) {
			throw new DollyFunctionalException(e.getRootCause().getMessage(), e);
		}
	}

//	private Set<Bruker> findOrCreateBrukere(List<RsBruker> rsBrukere) {
//		Set<Bruker> brukere = new HashSet<>();
//
//		for (RsBruker rsBruker : rsBrukere) {
//			Bruker bruker = brukerRepository.findBrukerByNavIdent(rsBruker.getNavIdent());
//
//			if (bruker == null) {
//				bruker= brukerRepository.save(mapperFacade.map(rsBruker, Bruker.class));
//			}
//
//			brukere.add(bruker);
//		}
//		return brukere;
//	}
}
