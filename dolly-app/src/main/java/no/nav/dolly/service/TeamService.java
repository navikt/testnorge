package no.nav.dolly.service;

import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static java.util.Objects.isNull;
import static no.nav.dolly.util.CurrentNavIdentFetcher.getLoggedInNavIdent;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.RsBruker;
import no.nav.dolly.domain.resultset.RsOpprettTeam;
import no.nav.dolly.domain.resultset.RsTeam;
import no.nav.dolly.domain.resultset.RsTeamUtvidet;
import no.nav.dolly.exceptions.ConstraintViolationException;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BrukerRepository;
import no.nav.dolly.repository.TeamRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

//TODO Burde gjore at alle returnerer team istedenfor "json/POJO" representasjonen av de.

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final BrukerRepository brukerRepository;
    private final BrukerService brukerService;
    private final MapperFacade mapperFacade;

    public RsTeamUtvidet opprettTeam(RsOpprettTeam opprettTeam) {
        Bruker currentBruker = brukerService.fetchBruker(getLoggedInNavIdent());

        Team team = saveTeamToDB(Team.builder()
                .navn(opprettTeam.getNavn())
                .beskrivelse(opprettTeam.getBeskrivelse())
                .datoOpprettet(LocalDate.now())
                .eier(currentBruker)
                .medlemmer(singletonList(currentBruker))
                .build()
        );

        return mapperFacade.map(team, RsTeamUtvidet.class);
    }

    public RsTeamUtvidet getTeamById(Long id) {
        return mapperFacade.map(fetchTeamById(id), RsTeamUtvidet.class);
    }

    public Team fetchTeamOrOpprettBrukerteam(Long teamId) {
        if (isNull(teamId)) {
            try {
                return fetchTeamByNavn(getLoggedInNavIdent());
            } catch (NotFoundException e) {
                Bruker bruker = brukerService.fetchBruker(getLoggedInNavIdent());
                Team team = new Team(getLoggedInNavIdent(), bruker);
                brukerService.leggTilTeam(bruker, team);
                return saveTeamToDB(team);
            }
        } else {
            return fetchTeamById(teamId);
        }
    }

    public int deleteTeam(Long teamId) {
        return teamRepository.deleteTeamById(teamId);
    }

    public RsTeam addMedlemmer(Long teamId, List<RsBruker> navIdenter) {
        Team team = fetchTeamById(teamId);
        team.getMedlemmer().addAll(mapperFacade.mapAsList(navIdenter, Bruker.class));

        Team changedTeam = saveTeamToDB(team);
        return mapperFacade.map(changedTeam, RsTeam.class);
    }

    public RsTeamUtvidet addMedlemmerByNavidenter(Long teamId, List<String> navIdenter) {
        Team team = fetchTeamById(teamId);
        List<Bruker> brukere = brukerRepository.findByNavIdentInOrderByNavIdent(navIdenter);

        team.getMedlemmer().addAll(brukere);

        Team changedTeam = saveTeamToDB(team);
        return mapperFacade.map(changedTeam, RsTeamUtvidet.class);
    }

    public RsTeamUtvidet fjernMedlemmer(Long teamId, List<String> navIdenter) {
        Team team = fetchTeamById(teamId);
        if (!team.getMedlemmer().isEmpty()) {
            team.getMedlemmer().removeIf(medlem -> navIdenter.contains(medlem.getNavIdent()));
        }

        Team changedTeam = saveTeamToDB(team);
        return mapperFacade.map(changedTeam, RsTeamUtvidet.class);
    }

    public RsTeamUtvidet slettMedlem(Long teamId, String navIdent) {
        Team team = fetchTeamById(teamId);
        boolean found = false;
        Iterator<Bruker> brukerIterator = team.getMedlemmer().iterator();
        while (brukerIterator.hasNext()) {
            Bruker bruker1 = brukerIterator.next();
            if (bruker1.getNavIdent().equalsIgnoreCase(navIdent)) {
                brukerIterator.remove();
                found = true;
                break;
            }
        }

        if (!found) {
            throw new NotFoundException(format("Bruker med ident %s ble ikke funnet.", navIdent));
        }

        Team changedTeam = saveTeamToDB(team);
        return mapperFacade.map(changedTeam, RsTeamUtvidet.class);
    }

    public RsTeamUtvidet updateTeamInfo(Long teamId, RsTeamUtvidet teamRequest) {
        Team team = fetchTeamById(teamId);

        team.setNavn(teamRequest.getNavn());
        team.setBeskrivelse(teamRequest.getBeskrivelse());

        if (!teamRequest.getMedlemmer().isEmpty()) {
            team.getMedlemmer().addAll(mapperFacade.mapAsSet(teamRequest.getMedlemmer(), Bruker.class));
        }

        if (isNotBlank(teamRequest.getEierNavIdent())) {
            team.setEier(brukerService.fetchBruker(teamRequest.getEierNavIdent()));
        }

        if (!teamRequest.getGrupper().isEmpty()) {
            team.getGrupper().addAll(mapperFacade.mapAsSet(teamRequest.getGrupper(), Testgruppe.class));
        }

        Team endretTeam = saveTeamToDB(team);
        return mapperFacade.map(endretTeam, RsTeamUtvidet.class);
    }

    public List<RsTeam> fetchTeamsByMedlemskapInTeamsMapped(String navIdent) {
        return mapperFacade.mapAsList(fetchTeamsByMedlemskapInTeams(navIdent), RsTeam.class);
    }

    public List<Team> fetchTeamsByMedlemskapInTeams(String navIdent) {
        return teamRepository.findByMedlemmerNavIdentOrderByNavn(navIdent);
    }

    public List<RsTeam> findAllOrderByNavn() {
        return mapperFacade.mapAsList(teamRepository.findAllByOrderByNavn(), RsTeam.class);
    }

    public Team fetchTeamById(Long id) {
        Optional<Team> team = teamRepository.findById(id);
        if (!team.isPresent()) {
            throw new NotFoundException("Team ikke funnet for denne IDen: " + id);
        }

        team.get().getMedlemmer().sort((Bruker br1, Bruker br2) -> br1.getNavIdent().compareToIgnoreCase(br2.getNavIdent()));
        return team.get();
    }

    private Team saveTeamToDB(Team team) {
        try {
            return teamRepository.save(team);
        } catch (DataIntegrityViolationException e) {
            throw new ConstraintViolationException("En Team DB constraint er brutt! Kan ikke lagre Team. Error: " + e.getMessage(), e);
        } catch (NonTransientDataAccessException e) {
            throw new DollyFunctionalException(e.getMessage(), e);
        }
    }

    private Team fetchTeamByNavn(String navn) {
        return teamRepository.findByNavn(navn).orElseThrow(() -> new NotFoundException("Team ikke funnet for dette navnet: " + navn));
    }
}
