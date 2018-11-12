package no.nav.dolly.service;

import static no.nav.dolly.util.CurrentNavIdentFetcher.getLoggedInNavIdent;
import static no.nav.dolly.util.UtilFunctions.isNullOrEmpty;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.RsBruker;
import no.nav.dolly.domain.resultset.RsOpprettTeam;
import no.nav.dolly.domain.resultset.RsTeam;
import no.nav.dolly.exceptions.ConstraintViolationException;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BrukerRepository;
import no.nav.dolly.repository.TeamRepository;

//TODO Burde gjore at alle returnerer team istedenfor "json/POJO" representasjonen av de.

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
        Bruker currentBruker = brukerService.fetchBruker(getLoggedInNavIdent());

        Team team = mapperFacade.map(rsTeam, Team.class);
        team.setDatoOpprettet(LocalDate.now());
        team.setEier(currentBruker);
        team.setMedlemmer(new HashSet<>(Arrays.asList(currentBruker)));

        Team savedTeam = saveTeamToDB(team);
        return mapperFacade.map(savedTeam, RsTeam.class);
    }

    public Team fetchTeamById(Long id) {
        return teamRepository.findById(id).orElseThrow(()-> new NotFoundException("Team ikke funnet for denne IDen: " + id));
    }

    public Team fetchTeamByNavn(String navn) {
        return teamRepository.findByNavn(navn).orElseThrow(()-> new NotFoundException("Team ikke funnet for dette navnet: " + navn));
    }

    @Transactional
    public Team fetchTeamOrOpprettBrukerteam(Long teamId){
        if(isNullOrEmpty(teamId)){
            try{
                return fetchTeamByNavn(getLoggedInNavIdent());
            } catch (NotFoundException e){
                Bruker bruker = brukerService.fetchBruker(getLoggedInNavIdent());
                Team t = new Team(getLoggedInNavIdent(),bruker);
                brukerService.leggTilTeam(bruker, t);
                return saveTeamToDB(t);
            }
        } else {
            return fetchTeamById(teamId);
        }
    }

    public void deleteTeam(Long id) {
        teamRepository.deleteById(id);
    }

    @Transactional
    public RsTeam addMedlemmer(Long teamId, List<RsBruker> navIdenter) {
        Team team = fetchTeamById(teamId);
        team.getMedlemmer().addAll(mapperFacade.mapAsList(navIdenter, Bruker.class));

        Team changedTeam = saveTeamToDB(team);
        return mapperFacade.map(changedTeam, RsTeam.class);
    }

    @Transactional
    public RsTeam addMedlemmerByNavidenter(Long teamId, List<String> navIdenter) {
        Team team = fetchTeamById(teamId);
        List<Bruker> brukere = brukerRepository.findByNavIdentIn(navIdenter);

        team.getMedlemmer().addAll(brukere);

        Team changedTeam = saveTeamToDB(team);
        return mapperFacade.map(changedTeam, RsTeam.class);
    }

    @Transactional
    public RsTeam fjernMedlemmer(Long teamId, List<String> navIdenter) {
        Team team = fetchTeamById(teamId);
        if (!isNullOrEmpty(team.getMedlemmer())) {
            team.getMedlemmer().removeIf(medlem -> navIdenter.contains(medlem.getNavIdent()));
        }

        Team changedTeam = saveTeamToDB(team);
        return mapperFacade.map(changedTeam, RsTeam.class);
    }

    @Transactional
    public RsTeam updateTeamInfo(Long teamId, RsTeam teamRequest) {
        Team team = fetchTeamById(teamId);

        team.setNavn(teamRequest.getNavn());
        team.setBeskrivelse(teamRequest.getBeskrivelse());

        if(!isNullOrEmpty(teamRequest.getMedlemmer())) {
            team.setMedlemmer(mapperFacade.mapAsSet(teamRequest.getMedlemmer(), Bruker.class));
        }

        if(!isNullOrEmpty(teamRequest.getEierNavIdent())) {
            team.setEier(brukerService.fetchBruker(teamRequest.getEierNavIdent()));
        }

        if(!isNullOrEmpty(teamRequest.getGrupper())){
            team.setGrupper(mapperFacade.mapAsSet(teamRequest.getGrupper(), Testgruppe.class));
        }

        Team endretTeam = saveTeamToDB(team);
        return mapperFacade.map(endretTeam, RsTeam.class);
    }

    public List<Team> fetchTeamsByMedlemskapInTeams(String navIdent) {
        return teamRepository.findByMedlemmer_NavIdent(navIdent);
    }

    public Team saveTeamToDB(Team team) {
        try {
            return teamRepository.save(team);
        } catch (DataIntegrityViolationException e) {
            throw new ConstraintViolationException("En Team DB constraint er brutt! Kan ikke lagre Team. Error: " + e.getMessage(), e);
        } catch (NonTransientDataAccessException e) {
            throw new DollyFunctionalException(e.getRootCause().getMessage(), e);
        }
    }
}
