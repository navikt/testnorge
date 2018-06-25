package no.nav.service;

import ma.glasnost.orika.MapperFacade;
import no.nav.jpa.Testgruppe;
import no.nav.resultSet.RsBruker;
import no.nav.resultSet.RsTeam;
import no.nav.exceptions.ConstraintViolationException;
import no.nav.exceptions.DollyFunctionalException;
import no.nav.exceptions.NotFoundException;
import no.nav.jpa.Bruker;
import no.nav.jpa.Team;
import no.nav.dolly.repository.BrukerRepository;
import no.nav.dolly.repository.TeamRepository;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.NonTransientDataAccessException;
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
	public RsTeam opprettTeam(RsTeam rsTeam) {
		Team team = mapperFacade.map(rsTeam, Team.class);
		team.setDatoOpprettet(LocalDate.now());
		team.getMedlemmer().add(team.getEier());
		team.setEier(brukerService.fetchBruker(rsTeam.getEierNavIdent()));

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

	public void addMedlemmer(Long teamId, List<RsBruker> navIdenter) {
		Set<Bruker> nyeMedlemmer = findOrCreateBrukere(navIdenter);

		Team team = fetchTeamById(teamId);
		team.getMedlemmer().addAll(nyeMedlemmer);

		saveToTeamRepository(team);
	}

	public Team updateTeamInfo(RsTeam teamRequest) {
		Team savedTeam = teamRepository.findTeamById(teamRequest.getId());

		savedTeam.setNavn(teamRequest.getNavn());
		savedTeam.setBeskrivelse(teamRequest.getBeskrivelse());
		savedTeam.setDatoOpprettet(teamRequest.getDatoOpprettet());

		if(teamRequest.getMedlemmer() == null){
			teamRequest.setMedlemmer(new HashSet<>());
		}

		Set<Bruker> medlemmer = mapperFacade.mapAsSet(teamRequest.getMedlemmer(), Bruker.class);
		savedTeam.setMedlemmer(medlemmer);

	    Bruker endretEier = brukerService.fetchBruker(teamRequest.getEierNavIdent());
	    savedTeam.setEier(endretEier);

	    if(teamRequest.getGrupper() == null){
	    	teamRequest.setGrupper(new HashSet<>());
		}

	    Set<Testgruppe> grupper = mapperFacade.mapAsSet(teamRequest.getGrupper(), Testgruppe.class);
	    savedTeam.setGrupper(grupper);

		return saveToTeamRepository(savedTeam);
	}

	public Team fjernMedlemmer(Long teamId, List<RsBruker> navIdenter) {
		Team team = fetchTeamById(teamId);
		if (!team.getMedlemmer().isEmpty()) {
			team.getMedlemmer().removeIf(medlem -> navIdenter.stream().anyMatch(rsBruker -> rsBruker.getNavIdent().equals(medlem.getNavIdent())));
		}

		return saveToTeamRepository(team);
	}

	public List<Team> fetchTeamsByMedlemskapInTeams(String navIdent){
		return teamRepository.findByMedlemmer_NavIdent(navIdent);
	}

	private Team saveToTeamRepository(Team team) {
		try {
			return teamRepository.save(team);
		} catch (DataIntegrityViolationException e) {
			throw new ConstraintViolationException("En Team DB constraint er brutt! Kan ikke lagre Team");
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
}
